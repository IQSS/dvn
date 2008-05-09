This short README describes the process of ingesting
digitized audio tapes into the DVN studies in a batch mode. 

1. Source Files

The process, as it is organized now, involves the members of
the project digitizing the tapes and storing them on the
shared network filesystem. From the DVN production server
this directory can be accessed as /nfs/mra/VDC/Murray. 

Each study is assigned a directory that usually contains the
number (id) of the study and the name of the author. For
example, Osherson_01116. Each individual audio file
(usually) has the filename of the form
A_<DDDDD>_<NNNN>_<C>.wav, where <DDDDN> is the study id,
<NNNN> is the tape number and <C> is a single-character tag
indicating the side of the tape (A or B). For example,
A_01116_0137_A.wav. 

In addition to the audio files a stylesheet is supplied with
the file-level metadata. 

2. Conversion to MP3. 

The size of uncompressed WAV files makes them very
impractical to use. Converting them to MP3 reduces the size
10-20 times with very little or no detectable loss in
quality. 

To convert the file I use lame, an open source audio
encoding utility. I use a simple shell script (convert,sh in
this directory) that iterates through all the WAV files in
the current directory, runs the conversion and stores the
resulting MP3 in the destination directory (I usually place
them directly into the study files directory on the DVN
filesystem). The wav files are kept in the original upload
directory for archiving. (i.e., the idea is that the MP3s
are made available for the users and the highest-quality WAV
files are archived). 

The speed of audio encoding per CPU is, very roughly, 1
minute CPU time per 10 minutes of audio. So a single audio
file of a standard 45 min. audio tape side takes 4-5
minutes. So for the Osherson/01116 study above with 528
audio files should take about 2400 minutes or 40 hours, if
run continuously on a single CPU. The task is of course
easily parallelized by processing different files on
different servers. I prefer not to run more than one conversion
process at a time on production to avoid slowing down the
performance of the DVN. 

3. Batch DVN ingest procedure. 

Ideally, it would be nice to be able to read and parse the
XLS stylesheet with the file-level metadata directly. I
haven't found a library that would easily do this for me, so
I've been manually opening the stylesheets and saving them
as tab-delimited files. There are 5 fields in the resulting
file: 

1. Tape Identifier 
2. Label (usually empty)
3. Description
4. Permissions (usually, "Murray Restricted")
5. Sides tag 

The last tag indicates if both sides are present; for
example, it can be "A + B.wav" indicating that this is a
double-sided tape and both sides have been successfully
digitized or "A.wav; side B blank", etc. 

An example of a metadata tab-delimited line: 

A01116_0007             "2049 11/22/78 Side 6 of 8 B side: 2049 11/22/78 side 7 of 8  Box spine: 2049 11/22/78 Sides 6, 7 Time 1 (B side pgm starts c. 13:56)"  Murray Restricted       A + B.wav

I have a script (script_check_audio.pl) that parses this
file, checks if the audio files referenced in it actually
exist and creates 2 text files: ingest.metadata, with the
parsed metadata fields ready to be ingested into the DVN
databases and files.missing, listing the audio files
referenced in the stylesheet, but not found in the
directory. 

So the metadata stylesheet line above results in 2 lines in
the ingest.metadata file: 

(description fields truncated for brevity)

A_01116_0007_A.mp3  /nfs/vdc/DVN/data/1902.1/01116/A_01116_0007_A.mp3	audio/x-mp3      2049 11/22/78		Side ... 
A_01116_0007_B.mp3  /nfs/vdc/DVN/data/1902.1/01116/A_01116_0007_B.mp3	audio/x-mp3      2049 11/22/78		Side ...


The next step requires creating a FileCategory in the
corresponding study -- unless there there's already a
category for digitized audio. script_create_filecategory.pl
script is used. (So the process involves looking at the file
categories already existing for this study and making this
decision). For the Osherson study above I created such
category: 

./script_create_filecategory.pl 204 '5. Audio Files'

(204 is the table id of the study and '5. Audio Files' is
the name I chose for the category). 

Finally, we can put the generated metadata into the
studyfile table with the script_ingest_audio.pl script:

cat ingest.metadata | ./script_ingest_audio.pl 188481

(188481 is the DVN id of the category I created in the
previous step).

