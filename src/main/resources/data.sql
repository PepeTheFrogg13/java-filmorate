INSERT INTO "Genre" ("Name") VALUES ('Комедия');
INSERT INTO "Genre" ("Name") VALUES ('Драма');
INSERT INTO "Genre" ("Name") VALUES ('Мультфильм');
INSERT INTO "Genre" ("Name") VALUES ('Триллер');
INSERT INTO "Genre" ("Name") VALUES ('Документальный');
INSERT INTO "Genre" ("Name") VALUES ('Боевик');

INSERT INTO "Rating" ("Name") VALUES ('G');
INSERT INTO "Rating" ("Name") VALUES ('PG');
INSERT INTO "Rating" ("Name") VALUES ('PG-13');
INSERT INTO "Rating" ("Name") VALUES ('R');
INSERT INTO "Rating" ("Name") VALUES ('NC-17');

INSERT INTO "FriendStatus" ("Name") VALUES ('Запрошено');
INSERT INTO "FriendStatus" ("Name") VALUES ('Принято');


INSERT INTO "User" ("Email","Login","Birthday") VALUES ('pepefrog@yandex.ru','Pepe','2006-03-09');
INSERT INTO "User" ("Email","Login","Name","Birthday") VALUES ('arbuz13@yandex.ru','Watermelon','Arbuzitto','2005-04-17');
INSERT INTO "User" ("Email","Login","Name","Birthday") VALUES ('mikhail_prakticum@yandex.ru','mikhail','Misha','2000-01-27');

INSERT INTO "UserFriends" ("UserSenderId","UserRecipientId","StatusId") VALUES (1,2,1);
INSERT INTO "UserFriends" ("UserSenderId","UserRecipientId","StatusId") VALUES (1,3,2);
INSERT INTO "UserFriends" ("UserSenderId","UserRecipientId","StatusId") VALUES (2,3,2);

INSERT INTO "Film" ("Name","Description","ReleaseDate","Duration","RatingId") VALUES ('Дедпул','Тест','2016-02-11',108,1);
INSERT INTO "Film" ("Name","Description","ReleaseDate","Duration","RatingId") VALUES ('1+1','Тест','2011-02-26',112,3);

INSERT INTO "FilmGenre" ("FilmId","GenreId") VALUES (1,1);
INSERT INTO "FilmGenre" ("FilmId","GenreId") VALUES (1,2);
INSERT INTO "FilmGenre" ("FilmId","GenreId") VALUES (1,3);

INSERT INTO "FilmLikes" ("FilmId","UserID") VALUES (1,1);