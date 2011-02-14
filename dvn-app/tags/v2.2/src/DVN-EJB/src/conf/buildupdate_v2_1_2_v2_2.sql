Alter Table dataTable
add visualizationEnabled boolean;

update dataTable set  visualizationEnabled = false;


INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditStudyFilesPage','/study/EditStudyFilesPage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'SetUpDataExplorationPage','/study/SetUpDataExplorationPage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ExploreDataPage','/viz/ExploreDataPage.xhtml',null,null );
