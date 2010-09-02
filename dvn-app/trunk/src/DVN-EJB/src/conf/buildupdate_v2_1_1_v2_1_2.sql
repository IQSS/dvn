
Alter Table vdcnetwork
add requireDVDescription boolean,
add requireDVaffiliation boolean,
add requireDVclassification boolean,
add requireDVstudiesforrelease boolean;

update vdcnetwork set  requireDVDescription = false,
 requireDVaffiliation = false,
 requireDVclassification = false,
 requireDVstudiesforrelease = false;

