ALTER TABLE vdcuser ADD COLUMN encryptedpassword text;


update vdcuser set encryptedpassword = 'tf0bLmzOFx5JrBhe2EIraS5GBnI=' where username='networkAdmin';