/*
 * cut implementation for busybox
 *
 * Copyright (c) Michael J. Holme
 *
 * This version of cut is adapted from Minix cut and was modified
 * by Erik Andersen <andersee@debian.org> to be used in busybox.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Original copyright notice is retained at the end of this file.
 */

/* Modified by Micah Altman to support multi-line records, purged cruft. */
/* modified by a.sone to support row-wise subsetting, decimalization, etc */
/*
	Unlike most versions of cruft, this preserves the order
	of the list spec in the output, and permits overlapping
	list specs, it also inserts delimiters in column cuts, if
	desired, and allows multi-line specs.

for example:
% cat test
1234567
ABCDEFG
abcdefg
1234567
ABCDEFG
abcdefg
1234567
ABCDEFG
abcdefg

% ./rcut -r3 -n7 -c'1:1,1:2-4,3:3-4,2:3' -o, < testr
1,234,cd,C
1,234,cd,C
1,234,cd,C

*/

#include <sys/types.h>
#include <ctype.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <getopt.h>
#include <limits.h>
#include <math.h>

#define MAX_FIELD	32767	/* Pointers to the beginning of each field
				* are stored in columns[], if a line holds
				* more than MAX_FIELD columns the array
				* boundary is exceed. But unlikely at 80 */

#define MAX_ARGS	10000 	/* Maximum number of fields following -f or
				* -c switches */
#define NBUFSIZ 32767
#define RSSIZE 256
#define DBG 0

int OPTF =0 ;
int OPTC =0;
int OPTS =0;
int OPTL =0;
int RECLEN = 0;
int RECNUM= 0;
long int PIDNO = -1;
char PIDNOS[257] ="-1";
char DELIM = '\t';	/* default delimiting character   */
char ODELIM = '\t';
char LINE[NBUFSIZ];
char LINE_BUFFERED[NBUFSIZ]; 
/*int  bytes_read = 0;*/
int args[MAX_ARGS];
int rsse[MAX_ARGS];
int argsn[MAX_ARGS];
char RSSET[RSSIZE];
int num_args=0;
int num_sets=0;
int crrntRN =0;
int DEBUG=0;
int DCMLADJ=1;

/* Defines for the fatal errors                     */
#define SYNTAX_ERROR                101
#define POSITION_ERROR              102
#define LINE_TO_LONG_ERROR          103
#define RANGE_ERROR                 104
#define MAX_FIELDS_EXEEDED_ERROR    105
#define MAX_ARGS_EXEEDED_ERROR      106

void cuterror(int err)
{
    static char *err_mes[] = {
        "%s: syntax error\n",
        "%s: position must be >0\n",
        "%s: line longer than NBUFSIZ\n",
        "%s: range must not decrease from left to right\n",
        "%s: MAX_FIELD exceeded\n",
        "%s: MAX_ARGS exceeded\n"
    };

    fprintf(stderr, err_mes[err - 101], "fcut");
    exit(err);
}

void usage() {
	fprintf(stderr, "cut [OPTIONS]\n"
	"\n Stripped 'cut'. Keeps fields in order. Inserts delimiters.\n\n"
	"\t-c LIST\tOutput only characters from LIST\n"
	"\t-d CHAR\tUse CHAR instead of tab as the field delimiter\n"
	"\t-s Only output Lines if the include DELIM\n"
	"\t-f N\tPrint only these fields from LIST\n"
	"\t-o use specified output delimiter\n"
	"\t-r # number of records\n"
	"\t-n # length of record\n"
	"\t-m ID number for subdividing a file/naming subfiles\n"
	"\t-g N1-N2 (N1<N2): a range param for case(row)-wise subsetting\n"
	);
	exit (1);
}

/*
 * parse_lists() - parses a list and puts values into startpos and endpos.
 * valid list formats: N, N-, N-M, -M
 * more than one list can be seperated by commas
 */

static void parse_lists(char *lists){
	int EOL,BOL;
	char *ltok = NULL;
	char *ntok = NULL;
	char *junk;
	int s = 0, e = 0,recid;
	int l;

	int fmt;
	/* take apart the lists, one by one (they are seperated with commas */
	num_args=0;

	#if DBG
		if(DEBUG){printf("entering parse_lists\n");}
	#endif
	while ((ltok = strsep(&lists, ",")) != NULL) {
		#if DBG
			if(DEBUG){printf("token split by comma(ltok)=%s\n", ltok);}
		#endif
		fmt =0;
		/* it's actually legal to pass an empty list */
		if (strlen(ltok) == 0){
			continue;
		}
		/* get the record pos */
		ntok = strsep(&ltok, ":");
		#if DBG
			if(DEBUG){printf("\trecord number(ntok)=%s\n", ntok);}
		#endif

		if (ltok) {
			recid=	atoi(ntok);

			#if DBG
				if(DEBUG){
					printf("\tcheck the remaining part\n");
					printf("\trecord number(ntok)=%s\n", ntok);
					printf("\trecord number after atoi=%d\n", recid);
				}
			#endif

			if ((recid>RECNUM) || (recid<1)){
				cuterror(SYNTAX_ERROR);
			}

			BOL = (recid-1)*(RECLEN+1);
			EOL = recid*(RECLEN+1)-1;

			#if DBG
				if(DEBUG){printf("BOL=%d\tEOL=%d\n",BOL,EOL);}
			#endif

		} else {
			recid=0;
			BOL = 0;
			EOL = INT_MAX;
			ltok=ntok;
		}
		/* get the start pos */
		ntok = strsep(&ltok, "-");
		if (ntok == NULL) {
			cuterror(SYNTAX_ERROR);
		} else if (strlen(ntok) == 0) {
			s = BOL;

			#if DBG
				if(DEBUG){
					printf("\tstart_string number(ntok) is empty\n");
					printf("\tstart_string number(s)=BOL(%d)\n",s);
				}
			#endif

		} else {
			s = strtoul(ntok, &junk, 10) + BOL;

			#if DBG
				if(DEBUG){
					printf("\tset start_string number(s)=%d\n",s);
				}
			#endif

			if(e < 0){
				cuterror(SYNTAX_ERROR);
			} else if (strlen(junk)) {

				if (*junk == '@'){
					junk++;
					if (*junk == 'c')  {
						fmt = -1;
					} else {
						/* */
						fmt = atoi(junk);
					}
					#if DBG
						if(DEBUG){
							printf("\thit the at mark\n");
							printf("\tset start_string number(s)=%d\n",s);
							printf("\tfrmt string=%s\n", junk);
							printf("\tformat code=%d\n", fmt);
						}
					#endif
				}
			} else if (*junk != '\0' ) {
				cuterror(SYNTAX_ERROR);
			}
		}

		/* get the end pos */
		ntok = strsep(&ltok, "-");

		if (ntok == NULL) {
			e = s;
			#if DBG
				if(DEBUG){
					printf("\tend string(e) is NULL\n");
					printf("\tset end string(e)=%d[start_string(s)]\n", e);
				}
			#endif

		} else if (strlen(ntok) == 0) {
			e = EOL;
			#if DBG
				if(DEBUG){
					printf("\tend string(e):length is 0\n");
					printf("\tset end string(e)=%d[EOL]\n", e);
				}
			#endif

		} else {
			e = strtoul(ntok, &junk, 10) + BOL;

			#if DBG
				if(DEBUG){
					printf("\tend_string after ul=%d\n",e);
					printf("\tcheck remaining string=%s\n",junk);
				}
			#endif

			if(e < 0){
				cuterror(SYNTAX_ERROR);
			} else if (e == 0) {
				/* if the user specified and end position of 0, that means "til the
				 * end of the line */
				e = EOL;

				#if DBG
					if(DEBUG){
						printf("\tend_string is 0\n");
						printf("\tset end_string(e)=%d[EOL]\n", e);
					}
				#endif

			} else if (strlen(junk)) {
				if (*junk == '@'){
					junk++;
					if (*junk == 'c')  {
						fmt = -1;
					} else {
						fmt = atoi(junk);
					}
					#if DBG
						if(DEBUG){
							printf("\thit the at mark\n");
							printf("\tset start_string number(s)=%d\n",s);
							printf("\tfrmt string=%s\n", junk);
							printf("\tformat code=%d\n", fmt);
						}
					#endif
				}

			} else if (*junk != '\0' ) {
				cuterror(SYNTAX_ERROR);
			}
		}

		/* if there's something left to tokenize, the user past an invalid list */
		if (ltok){
			cuterror(SYNTAX_ERROR);
		}
		/*printf("s=%d\te=%d\n",s,e);*/

		/* add the new list */
		args[num_args*2]=s;
		args[num_args*2+1]=e;

		argsn[num_args]=fmt;


		#if DBG
			if (DEBUG){
				printf("no of arguments set(num_args)=%d\n", num_args);
				printf("contents of args array\n");
				for (l=0;args[l] != 0 ;l++){
					printf("%d:%d\n",l, args[l]);
				}
				printf("\n");

				printf("contents of accompanying format set\n");
				for (l=0;l<=(num_args) ;l++){
					printf("%d:%d\n",l, argsn[l]);
				}
				printf("\n");
			}
		#endif
		num_args++;


		if ((num_args*2+1)>= MAX_ARGS){
			cuterror(MAX_ARGS_EXEEDED_ERROR);
		}
	}
		if(DEBUG){
			printf("no of column no pairs(num_args)=%d\n", num_args);
			printf("\ncolumn-subsetting array\n");
			for (l=0;args[l] != 0 ;l++){
				printf("%d:%d\n",l, args[l]);
			}
			printf("\ncontents of frmt array\n");
			for (l=0;l<=(num_args) ;l++){
				printf("%d:%d\n",l, argsn[l]);
			}
			printf("\n");
		}

	/* make sure we got some cut positions out of all that */
	/* printf("entering last of parse_lists\n");*/

	if (num_args == 0){
		cuterror(SYNTAX_ERROR);
	}
}


static void parse_list_g(char *lists){
	/*int EOL,BOL;*/
	char *ltok = NULL;
	char *ntok = NULL;
	char *junk;
	int s = 0, e = 0;
	int l;

	/* take apart the lists, one by one (they are seperated with commas */
	num_sets=0;
	#if DBG
		if(DEBUG){ printf("entering parse_list_g\n");}
	#endif

	while ((ltok = strsep(&lists, ",")) != NULL) {
		#if DBG
			if(DEBUG){printf("token split by comma(ltok)=%s\n", ltok);}
		#endif
		/* it's actually legal to pass an empty list */
		if (strlen(ltok) == 0){
			continue;
		}

		/* get the start pos */
		ntok = strsep(&ltok, "-");
		#if DBG
			if(DEBUG){printf("start_string(ntok)=%s\n", ntok);}
		#endif
		if (ntok == NULL) {
			#if DBG
				if(DEBUG){printf("\tstart_string(ntok) is NULL\n");}
   			#endif
			cuterror(SYNTAX_ERROR);
		} else {
			s = strtoul(ntok, &junk, 10);
			#if DBG
				if(DEBUG){printf("\tstart_string: after ulong =%d\n",s);}
			#endif
			if(e < 0){
				cuterror(SYNTAX_ERROR);
			} else if (*junk != '\0' ) {
				cuterror(SYNTAX_ERROR);
			}
		}

		/* get the end pos */
		ntok = strsep(&ltok, "-");
		#if DBG
			if(DEBUG){
				if (ntok != NULL){
					printf("\tend_string=%s\n", ntok);
				}
			}
		#endif
		if (ntok == NULL) {
			#if DBG
				if(DEBUG){printf("\tend_string is NULL: start_no = end_no\n");}
			#endif
			e = s;
		} else {
			e = strtoul(ntok, &junk, 10);
			#if DBG
				if (DEBUG){
					printf("\tend_string: after ulong=%d\n",e);
					printf("check the leftover string=%s\n", junk );
				}
			#endif
			if(e < 0){
				cuterror(SYNTAX_ERROR);
			} else if (*junk != '\0' ) {
				cuterror(SYNTAX_ERROR);
			}
		}
			#if DBG
				if (DEBUG){
					printf("entering the leftover processing\n");
				}
			#endif

		/* if there's something left to tokenize, the user past an invalid list */
		if (ltok){
			cuterror(SYNTAX_ERROR);
		}

		#if DBG
			if (DEBUG){
				printf("start_string=%d\tend_string=%d\n",s,e);
			}
		#endif

		/* add the new list */
		rsse[num_sets*2]=s;
		rsse[num_sets*2+1]=e;

		#if DBG
			if (DEBUG){
				printf("no of argument set(num_sets)=%d\n", num_sets);
				printf("contents of argment_set array\n");
				for (l=0;rsse[l] != 0 ;l++){
					printf("%d:%d\n",l, rsse[l]);
				}
			}
		#endif

		num_sets++;

		if ((num_sets*2+1)>= MAX_ARGS){
			cuterror(MAX_ARGS_EXEEDED_ERROR);
		}
	}

	/* make sure we got some cut positions out of all that */
	/* printf("entering last of parse_lists\n");*/
	if (num_sets <= 0){
		cuterror(SYNTAX_ERROR);
	} else {
		if(DEBUG){
			printf("no of case-subsetting num_sets=%d\n", num_sets);
			for (l=0;rsse[l] != 0 ;l++){
				printf("%d:%d\n",l, rsse[l]);
			}
			printf("\n");
		}
	}
}



void cut_col_line (char *input) {
	int i, j, length, maxcol=0;
	char buff[256];
	char* pbff;
	pbff = input;

	length = strlen(input) - 1;
	*(input+ length) = 0;
	crrntRN++;
	/* printf("/////////////// cut_col_line ///////////////\n"); */

	if(DEBUG){printf("row no=%08d= \t", crrntRN);}

	for (i = 0; i < num_args; i++) {
		buff[0]='\0';

		for (j = args[i * 2]; j <= (args[i * 2 + 1] > length ? length : args[i * 2 + 1] ); j++){
			sprintf(buff, "%s%c",buff, *(input+ j - 1));
		}

		/* decimalization: can be skipped if rs*/
	if (DCMLADJ){

		if (argsn[i] > 0){
			/* decimalization*/
			printf("%g", atof(buff)/(pow(10.0, argsn[i])));
		} else if (argsn[i] == 0){
			/* integer*/
			if (strchr(buff, '.')){
				printf("%s", buff);
			} else {
				printf("%ld", atol(buff));
			}
		} else  if (argsn[i] < 0) {
			/* character */
			printf("%s", buff);
		}
	} else {
		printf("%s", buff);
	}
		/* add separator: can be skipped if rs*/
		if ((i != num_args - 1) && (ODELIM != '\0') ){
			putchar(ODELIM);
		}

	}
	if (maxcol != 1){
		/* printf("++++++ current row number=%d ++++++\n", crrntRN);*/
		putchar('\n');
	}
}


void cut_col(FILE *fd){
	char input[NBUFSIZ];
	int jj=0;
	int frstobs=0;
	int lastobs=0;
	int ln =0;
	/*printf("/////////////// cut_col ///////////////\n");*/

	while (1) {
		if (RECNUM==0)  {
			if (!fgets(input, NBUFSIZ, fd)) break;
		} else  {
		  if ( LINE_BUFFERED[0] ) 
		    {
               		strcpy ( input, LINE_BUFFERED ); 
			LINE_BUFFERED[0] = '\000';
			if (!fread(input+RECLEN+1, 1, (RECLEN+1)*(RECNUM-1), fd)) break;
		    } 
		  else
		    {
			if (!fread(input , 1, (RECLEN+1)*RECNUM, fd)) break;
		    }
		}
		jj++;
		if (DEBUG){
			printf("\n++++++++++++++ %d-th iteration ++++++++++++++\n",jj);
		}
		if ( (num_sets) && (jj == 1) ) {
			frstobs=rsse[ln];
			lastobs=rsse[ln+1];
		}
		/* enter the following routine if (1) 1st range or (2) no g option (all cases used) */
		if (num_sets){
			if (DEBUG){
				printf("1st=%d\tlast=%d\n", frstobs, lastobs);
			}
			if ( (jj < frstobs) || (jj >lastobs)) {
				continue;
			}
			if (jj == lastobs){
				if (DEBUG){
					printf("within:jj=%d\t1st=%d\tlast=%d\n", jj, frstobs, lastobs);
				}
				if ( ((ln+2)/2) <= num_sets){
					ln=ln+2;
					frstobs=rsse[ln];
					lastobs=rsse[ln+1];
				}
			}
		}

		cut_col_line(input);

	}
}

void cut_fields(FILE *fd)
{
	int i, j, length, maxcol=0;
	char *columns[MAX_FIELD];
	char input[NBUFSIZ];
	char buff[256];
	int frstobs=0;
	int lastobs=0;
	int ln =0;
	int k,kk,jj;

	if(DEBUG){printf("/////////////// cut_fields ///////////////\n");}
	jj=0;
	while (fgets(input, NBUFSIZ, fd)) {
		jj++;
		if (DEBUG){
			printf("\n++++++++++++++ %d-th iteration ++++++++++++++\n",jj);
		}
	if ( (num_sets) && (jj == 1) ) {
		frstobs=rsse[ln];
		lastobs=rsse[ln+1];
	}
	if (num_sets){
		if (DEBUG){
			printf("1st=%d\tlast=%d\n", frstobs, lastobs);
		}
		if ( (jj < frstobs) || (jj >lastobs)) {
			continue;
		}
		if (jj == lastobs){
			if (DEBUG){
				printf("within:jj=%d\t1st=%d\tlast=%d\n", jj, frstobs, lastobs);
			}
			if ( ((ln+2)/2) <= num_sets){
				ln=ln+2;
				frstobs=rsse[ln];
				lastobs=rsse[ln+1];
			}
		}
	}

		k=0;
		kk=0;
		maxcol=0;
		length = strlen(input) - 1;
		/* length = a line of data without a new line character*/
		if (DEBUG){
			printf("length=%d\n",length);
		}
/*
		for(k=0;columns[k]!='\0';k++){
			printf("check k=%d\t",k);
			if (jj == 0){
				for (kk=0;columns[k][kk]!='\0';kk++){*/
			/*	printf("%d\n",strlen(columns[k]));
			}
			printf("- %d\n",kk);}
		}
		if(length >= MAX_FIELD){
			cuterror(MAX_FIELDS_EXEEDED_ERROR);
		}

*/

		*(input + length) = '\0';/*  input[length] = */
		columns[maxcol++] = input ;/*if maxcol=0, columns[0] = input then maxcol=1 */
		/*
			printf("maxcol=%d\n",maxcol);
			kk++;
			printf("kk=%d\n",kk);
		*/
/*
		if (DEBUG){
			printf("1st iteration\n");
			for(k=0;columns[k]!='\0';k++){
				printf("k=%d\t",k);
				for (kk=0;columns[k][kk]!='\0';kk++){
					printf("[%c]",columns[k][kk]);
				}
				printf("- %d\n",kk);
			}
		}
*/
		for (i = 0; i < length; i++) {
			if (*(input + i) == DELIM) {
				*(input+ i) = '\0';
				if (maxcol == MAX_FIELD){
					cuterror(MAX_FIELDS_EXEEDED_ERROR);
				}
				columns[maxcol] = input + i + 1;
				maxcol++;
			}
		}
		/* after the above block maxcol = no of vars in a line of data*/

/*
		if (DEBUG){
			printf("2nd iteration\n");
			printf("maxcol=%d\n\n",maxcol);
			for(k=0;columns[k]!='\0';k++){
				printf("k=%d\t",k);
				for (kk=0;columns[k][kk]!='\0';kk++){
					printf("[%c]",columns[k][kk]);
				}
				printf("- %d\n",kk);
			}
		}
*/
		if (maxcol != 1) {
		    for (i = 0; i < num_args; i++) {
				buff[0]='\0';

				for (j = args[i * 2]; j <= args[i * 2 + 1]; j++){
					if (j <= maxcol) {
						/* print a data point */
						printf("%s", columns[j - 1]);
						if (i != num_args - 1 || j != args[i * 2 + 1]){
							/* print a delimiter */
							putchar(ODELIM);
						}
					}
				}
			}
		} else if (OPTS==1) {
			printf("%s",input);
		}
		if (maxcol != 1){
			/* print a new line character */
			putchar('\n');
		}
/*
		if (DEBUG){
			printf("3rd iteration\n");
			for(k=0;columns[k]!='\0';k++){
				printf("k=%d\t%s\n", k, columns[k]);

					printf("k=%d\t",k);
					for (kk=0;columns[k][kk]!='\0';kk++){
						printf("[%c]",columns[k][kk]);
					}
					printf("- %d\n",kk);

			}
		}
*/
		columns[1]='\0';/* */
	}
}

void parse_args(int argc, char **argv) {
	int c;
	int odelim_set = 0;

	if (argc == 1 || strcmp(argv[1], "--help")==0) {
		usage();
	}

	while (1) {

		c = getopt(argc, argv, "c:d:f:o::g:r:n:m:s:y::z::");
		if (c == -1)
			break;

		switch (c) {
			case 'c':
			strncpy(LINE,optarg,NBUFSIZ);
			if(DEBUG){
				printf("\nc:line=%s\n", LINE);
				/*printf("c:length=%d\n\n",strlen(LINE));*/
			}
			OPTC = 1;
			break;

			case 'f':
			strncpy(LINE,optarg,NBUFSIZ);
			if(DEBUG){
				printf("\nf:line=%s\n", LINE);
				/*printf("f:length=%d\n\n",strlen(LINE));*/
			}
			OPTF = 1;
			break;

			case 'd':
			DELIM = optarg[0];
			break;

			case 'r':
			RECNUM = atoi(optarg);
			break;

			case 'n':
			RECLEN = atoi(optarg);
			break;

			case 'm':
			strncpy(PIDNOS,optarg,strlen(optarg));
			PIDNO = atoi(optarg);
			break;

			case 'o':
			odelim_set = 1;
			if (optarg) {
				ODELIM = optarg[0];
			}
			break;

			case 's':
			OPTS = 1;
			break;

			case 'g':
			strncpy(RSSET,optarg,RSSIZE);
			if (optarg) {
				OPTL=1;
			}
			if(DEBUG){
				printf("\ng:option=%s\n\n",RSSET );
				/*printf("l:length=%d\n\n",strlen(RSSET));*/
			}
			break;


			case 'y':
			DCMLADJ=0;
			break;

			case 'z':
			DEBUG=1;
			break;


		}
	}
	if (optind < argc) {
		fprintf (stderr ,
			"WARNING: accepts input only on stdin, ignoring files\n");
	}
	if (odelim_set==0) {
		if (OPTF) {
			ODELIM = DELIM;
		} else {
			ODELIM='\0';
		}
	}
	if (DEBUG){
		printf("RECNUM=%d\n",RECNUM);
		printf("RECLEN=%d\n",RECLEN);
	}
	if ( (RECLEN <0) ||
	(RECNUM<0) ||
	((RECLEN >0) && (RECNUM==0)) ||
	((RECNUM>0) && (OPTF)))  {
		fprintf (stderr ,
			"Error: must specify -c,n,r together");
		usage();
	}
	if ( ((OPTC==0) && (OPTF==0)) || ((OPTC==1) && (OPTF==1)) ) {
		usage();
	}
	if ( (OPTF==0) && (OPTS==1)) {
		fprintf (stderr,"WARNING: no -f specified, ignoring -s");
	}
}

void fcut_fields(FILE *fp, int PIDNO){
	int i, j, k,length, maxcol=0;
	char buff[256];
	char *columns[MAX_FIELD];
	char input[NBUFSIZ];
	int lenfln;
	char *tempfiles[MAX_ARGS];
	char *tempflno[MAX_ARGS];
	/*
	char *dir[]={"/tmp/", "./"};
	int dirno=1;
	*/

	char *fileparts[]={"t.",".",".tab"};
	char *md[] ={"w","a"};
	FILE *fw;
	int counter=0;
	int mode=0;
	char pid[6];
	int jj;
	int frstobs=0;
	int lastobs=0;
	int ln =0;
	sprintf(pid, "%d",PIDNO);

	/*
	printf("PIDNO= %d\n", PIDNO);
	printf("process ID= %s\n", pid);
	printf("num_args= %d\n", num_args);
	printf("fileparts[0]= %s\n", fileparts[0]);
	dirno=0;


	*/

	/* set up temp files*/
	for (k=0; k < num_args; k++) {
		tempflno[k]=malloc(4+1);

		if (tempflno[k] == NULL) {
			printf("Error: malloc failed in file no\n");
			exit(EXIT_FAILURE);
		}
		sprintf(tempflno[k],"%d", (k+1));
		/* lenfln =strlen(dir[dirno]) +2 + strlen(pid)+1 + strlen(tempflno[k])+4 + 1; */
		/* lenfln =strlen(dir[dirno]) +2 + strlen(PIDNOS)+1 + strlen(tempflno[k])+4 + 1;*/
		lenfln = strlen(PIDNOS)+ strlen(fileparts[1]) + strlen(tempflno[k])+ strlen(fileparts[2]) + 1;
		/* printf("filename length=%d\n",lenfln);*/
		tempfiles[k]= malloc(lenfln);

		if (tempfiles[k] == NULL) {
			printf("Error: malloc failed in file name\n");
			exit(EXIT_FAILURE);
		}
		/*

		strcpy(tempfiles[k], dir[dirno]);
		strcat(tempfiles[k], fileparts[0]);

		strcat(tempfiles[k], pid);



		*/
		strcat(tempfiles[k], PIDNOS);
		strcat(tempfiles[k], fileparts[1]);
		strcat(tempfiles[k], tempflno[k]);
		strcat(tempfiles[k], fileparts[2]);
		free(tempflno[k]);
	}
	jj=0;
	while (fgets(input, NBUFSIZ, fp)) {
		jj++;
		if (DEBUG){
			printf("\n\n++++++++++++++ %d-th iteration ++++++++++++++\n\n",jj);
		}
		if ( (num_sets) && (jj == 1) ) {
			frstobs=rsse[ln];
			lastobs=rsse[ln+1];
		}
		if (num_sets){
			printf("1st=%d\tlast=%d\n", frstobs, lastobs);
			if ( (jj < frstobs) || (jj >lastobs)) {
				continue;
			}
			if (jj == lastobs){

				printf("within:jj=%d\t1st=%d\tlast=%d\n", jj, frstobs, lastobs);
				if ( ((ln+2)/2) <= num_sets){
					ln=ln+2;
					frstobs=rsse[ln];
					lastobs=rsse[ln+1];
				}
			}
		}

		maxcol=0;
		length = strlen(input) - 1;
		*(input + length) = 0;
		columns[maxcol++] = input ;
		for (i = 0; i < length; i++) {
			if (*(input + i) == DELIM) {
				*(input+ i) = 0;
				if (maxcol == MAX_FIELD)
					cuterror(MAX_FIELDS_EXEEDED_ERROR);
				columns[maxcol] = input + i + 1;
				maxcol++;
			}
		}

		if (maxcol != 1) {
			if (counter > 0) {mode = 1;}
			for (i = 0; i < num_args; i++) {
				buff[0]='\0';
				/* printf("%d-th tempfile name=%s\n",i,tempfiles[i]);*/
				/* field set boundary */
				if ( (fw = fopen(tempfiles[i], md[mode])) == NULL){
					fprintf(stderr, "Error opening file.\n");
					exit(1);
				}
				for (j = args[i * 2]; j <= args[i * 2 + 1]; j++){
					if (j <= maxcol) {
						fprintf(fw,"%s", columns[j-1]);
						/* fprintf(fw, "%s", columns[j - 1]);*/
						if (i != num_args - 1 || j != args[i * 2 + 1]) {fputc(ODELIM, fw);}
					}
				}
					/* print newline character */

					fputc('\n', fw);
					fclose(fw);
				/* field set boundary */
			}
			counter++;

		} else if (OPTS==1) {
			if ( (fw = fopen("temp.1554.z.tab", "w")) == NULL){
				fprintf(stderr, "Error opening file.\n");
				exit(1);
			}
			fprintf(fw, "%s",input);
			fclose(fw);
		}
		columns[1]='\0';/* */
	}

	for (k=0;k<num_args;k++){
		free(tempfiles[k]);
	}
}

int main(int argc, char **argv){
	#if DBG
		int l;
	#endif
	parse_args(argc,argv);
	if (OPTL){
		if(DEBUG){printf("case-wise subsetting is requested\n\n");}
		parse_list_g(RSSET);
	}
	if ((RECNUM>0) && (RECLEN<1)) {
		/* no reclen specified must determine reclength from data*/
		char input[NBUFSIZ];

		fgets(input, NBUFSIZ, stdin);
		/* contents of input after fgets: data themselve + '\n' + '\0'*/
		RECLEN = strlen(input)-1;/*RECLEN includes a new line character */
		if (RECLEN<1) {
		   fprintf(stderr,"can't determine record length\n");
		   usage();
		}
		/*
		offset=ftell(stdin);
		if(DEBUG){printf("++++++ ftell=%d ++++++\n",offset);}
		*/
		/* move back the origin of th stream */
		/*
		fseek(stdin, 0,SEEK_SET);
		*/
		strcpy ( LINE_BUFFERED, input ); 
		/*bytes_buffered = RECLEN; */
		parse_lists(LINE);
		cut_col(stdin);
	} else {
		parse_lists(LINE);
	}
	if (OPTC==1) {
		cut_col(stdin);
		#if DBG
			if (DEBUG){
				printf("\ncontents of args array\n");
				for (l=0;args[l] != 0 ;l++){
					printf("%d-%d\n",l, args[l]);
				}
				printf("\n");
			}
		#endif
	}  else {
		if (PIDNO == -1) {
			cut_fields(stdin);
		} else if (PIDNO >= 0) {
			fcut_fields(stdin, PIDNO);
		}
	}
	if(DEBUG){printf("last row number=%d\n", crrntRN);}
	return(0);
}

/*
 * Copyright (c) 1987,1997, Prentice Hall
 * All rights reserved.
 *
 * Redistribution and use of the MINIX operating system in source and
 * binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * Neither the name of Prentice Hall nor the names of the software
 * authors or contributors may be used to endorse or promote
 * products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS, AUTHORS, AND
 * CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL PRENTICE HALL OR ANY AUTHORS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
