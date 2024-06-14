alter table USERS modify (PASSWD varchar2(4000 BYTE));
commit;

alter table ATTRIBUTEDESCRIPTORS add P_DEFAULTVALUE_TMP BLOB;
commit;

update ATTRIBUTEDESCRIPTORS set P_DEFAULTVALUE_TMP=P_DEFAULTVALUE where 1=1;
commit;

alter table ATTRIBUTEDESCRIPTORS drop column  P_DEFAULTVALUE;
commit;

alter table ATTRIBUTEDESCRIPTORS rename column P_DEFAULTVALUE_TMP to P_DEFAULTVALUE;
commit;

alter table ATTRIBUTEDESCRIPTORSLP modify (P_NAME varchar2(4000 BYTE));
commit;
