--To set the application to Read-only mode:
-- Note database and user strings may have to be modified for your particular installation
-- You may also customize the status notice which will appear on all pages of the application
BEGIN; SET TRANSACTION READ WRITE;
update vdcnetwork set statusnotice = 'This network is currently in Read Only state. No saving of data will be allowed.';
ALTER DATABASE "dvnDb" set default_transaction_read_only=on;
Alter user "dvnApp" set default_transaction_read_only=on;
END;



--To set the application back to normal mode:
-- Note database and user strings may have to be modified for your particular installation
BEGIN; SET TRANSACTION READ WRITE;
ALTER DATABASE "dvnDb" set default_transaction_read_only=off;
Alter user "dvnApp" set default_transaction_read_only=off;
update vdcnetwork set statusnotice = '';
END;