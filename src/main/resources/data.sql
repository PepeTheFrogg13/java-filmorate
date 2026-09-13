-- Жанры
MERGE INTO "Genre" ("GenreId", "Name") KEY("GenreId") VALUES (1, 'Комедия');
MERGE INTO "Genre" ("GenreId", "Name") KEY("GenreId") VALUES (2, 'Драма');
MERGE INTO "Genre" ("GenreId", "Name") KEY("GenreId") VALUES (3, 'Мультфильм');
MERGE INTO "Genre" ("GenreId", "Name") KEY("GenreId") VALUES (4, 'Триллер');
MERGE INTO "Genre" ("GenreId", "Name") KEY("GenreId") VALUES (5, 'Документальный');
MERGE INTO "Genre" ("GenreId", "Name") KEY("GenreId") VALUES (6, 'Боевик');

-- Рейтинги
MERGE INTO "Rating" ("RatingId", "Name") KEY("RatingId") VALUES (1, 'G');
MERGE INTO "Rating" ("RatingId", "Name") KEY("RatingId") VALUES (2, 'PG');
MERGE INTO "Rating" ("RatingId", "Name") KEY("RatingId") VALUES (3, 'PG-13');
MERGE INTO "Rating" ("RatingId", "Name") KEY("RatingId") VALUES (4, 'R');
MERGE INTO "Rating" ("RatingId", "Name") KEY("RatingId") VALUES (5, 'NC-17');

-- Статусы дружбы
MERGE INTO "FriendStatus" ("StatusId", "Name") KEY("StatusId") VALUES (1, 'Запрошено');
MERGE INTO "FriendStatus" ("StatusId", "Name") KEY("StatusId") VALUES (2, 'Принято');

-- Пользователи
MERGE INTO "User" ("UserID", "Email", "Login", "Name", "Birthday") KEY("UserID")
    VALUES (1, 'pepefrog@yandex.ru', 'Pepe', NULL, '2006-03-09');

MERGE INTO "User" ("UserID", "Email", "Login", "Name", "Birthday") KEY("UserID")
    VALUES (2, 'arbuz13@yandex.ru', 'Watermelon', 'Arbuzitto', '2005-04-17');

MERGE INTO "User" ("UserID", "Email", "Login", "Name", "Birthday") KEY("UserID")
    VALUES (3, 'mikhail_prakticum@yandex.ru', 'mikhail', 'Misha', '2000-01-27');

-- Дружба
MERGE INTO "UserFriends" ("UserFriendsId", "UserSenderId", "UserRecipientId", "StatusId") KEY("UserFriendsId")
    VALUES (1, 1, 2, 1);
MERGE INTO "UserFriends" ("UserFriendsId", "UserSenderId", "UserRecipientId", "StatusId") KEY("UserFriendsId")
    VALUES (2, 1, 3, 2);
MERGE INTO "UserFriends" ("UserFriendsId", "UserSenderId", "UserRecipientId", "StatusId") KEY("UserFriendsId")
    VALUES (3, 2, 3, 2);

-- Фильмы
MERGE INTO "Film" ("FilmId", "Name", "Description", "ReleaseDate", "Duration", "RatingId") KEY("FilmId")
    VALUES (1, 'Дедпул', 'Тест', '2016-02-11', 108, 1);

MERGE INTO "Film" ("FilmId", "Name", "Description", "ReleaseDate", "Duration", "RatingId") KEY("FilmId")
    VALUES (2, '1+1', 'Тест', '2011-02-26', 112, 3);

-- Жанры фильмов
MERGE INTO "FilmGenre" ("FilmGenreId", "FilmId", "GenreId") KEY("FilmGenreId") VALUES (1, 1, 1);
MERGE INTO "FilmGenre" ("FilmGenreId", "FilmId", "GenreId") KEY("FilmGenreId") VALUES (2, 1, 2);
MERGE INTO "FilmGenre" ("FilmGenreId", "FilmId", "GenreId") KEY("FilmGenreId") VALUES (3, 1, 3);

-- Лайки
MERGE INTO "FilmLikes" ("FilmLikesId", "FilmId", "UserID") KEY("FilmLikesId") VALUES (1, 1, 1);

ALTER TABLE "User" ALTER COLUMN "UserID" RESTART WITH 4;
ALTER TABLE "Film" ALTER COLUMN "FilmId" RESTART WITH 3;
ALTER TABLE "UserFriends" ALTER COLUMN "UserFriendsId" RESTART WITH 4;
ALTER TABLE "FilmGenre" ALTER COLUMN "FilmGenreId" RESTART WITH 4;
ALTER TABLE "FilmLikes" ALTER COLUMN "FilmLikesId" RESTART WITH 2;
ALTER TABLE "Rating" ALTER COLUMN "RatingId" RESTART WITH 6;
ALTER TABLE "Genre" ALTER COLUMN "GenreId" RESTART WITH 7;
ALTER TABLE "FriendStatus" ALTER COLUMN "StatusId" RESTART WITH 3;