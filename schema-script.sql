
    create table content_tag (
       content_guid varchar(255) not null,
        tag_guid varchar(255) not null
    );

    create table contents (
       guid varchar(255) not null,
        contenturl varchar(255),
        tenantguid varchar(255),
        transcript varchar(255),
        primary key (guid)
    );

    create table tags (
       guid varchar(255) not null,
        name varchar(255),
        primary key (guid)
    );

    alter table content_tag 
       add constraint FKh9vt45kepyh4s6x3m705h0eem 
       foreign key (tag_guid) 
       references tags;

    alter table content_tag 
       add constraint FK2l23mx5lwgru4utx585oqaif 
       foreign key (content_guid) 
       references contents
