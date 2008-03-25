ALTER TABLE studygrant ALTER COLUMN number type text;
ALTER TABLE studygrant ALTER COLUMN number SET STORAGE EXTENDED;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ForgotPasswordPage', '/login/ForgotPasswordPage.jsp', null,null );
