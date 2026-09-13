-- Жанры
INSERT INTO "Genre" ("GenreId", "Name") VALUES (1, 'Комедия');
INSERT INTO "Genre" ("GenreId", "Name") VALUES (2, 'Драма');
INSERT INTO "Genre" ("GenreId", "Name") VALUES (3, 'Мультфильм');
INSERT INTO "Genre" ("GenreId", "Name") VALUES (4, 'Триллер');
INSERT INTO "Genre" ("GenreId", "Name") VALUES (5, 'Документальный');
INSERT INTO "Genre" ("GenreId", "Name") VALUES (6, 'Боевик');

-- Рейтинги
INSERT INTO "Rating" ("RatingId", "Name") VALUES (1, 'G');
INSERT INTO "Rating" ("RatingId", "Name") VALUES (2, 'PG');
INSERT INTO "Rating" ("RatingId", "Name") VALUES (3, 'PG-13');
INSERT INTO "Rating" ("RatingId", "Name") VALUES (4, 'R');
INSERT INTO "Rating" ("RatingId", "Name") VALUES (5, 'NC-17');

-- Статусы дружбы
INSERT INTO "FriendStatus" ("StatusId", "Name") VALUES (1, 'Запрошено');
INSERT INTO "FriendStatus" ("StatusId", "Name") VALUES (2, 'Принято');

-- Пользователи
INSERT INTO "User" ("UserID", "Email", "Login", "Birthday")
VALUES (1, 'pepefrog@yandex.ru', 'Pepe', '2006-03-09');

INSERT INTO "User" ("UserID", "Email", "Login", "Name", "Birthday")
VALUES (2, 'arbuz13@yandex.ru', 'Watermelon', 'Arbuzitto', '2005-04-17');

INSERT INTO "User" ("UserID", "Email", "Login", "Name", "Birthday")
VALUES (3, 'mikhail_prakticum@yandex.ru', 'mikhail', 'Misha', '2000-01-27');

-- Дружба
INSERT INTO "UserFriends" ("UserFriendsId", "UserSenderId", "UserRecipientId", "StatusId")
VALUES (1, 1, 2, 1);
INSERT INTO "UserFriends" ("UserFriendsId", "UserSenderId", "UserRecipientId", "StatusId")
VALUES (2, 1, 3, 2);
INSERT INTO "UserFriends" ("UserFriendsId", "UserSenderId", "UserRecipientId", "StatusId")
VALUES (3, 2, 3, 2);

-- Фильмы
INSERT INTO "Film" ("FilmId", "Name", "Description", "ReleaseDate", "Duration", "RatingId")
VALUES (1, 'Дедпул', 'Тест', '2016-02-11', 108, 1);

INSERT INTO "Film" ("FilmId", "Name", "Description", "ReleaseDate", "Duration", "RatingId")
VALUES (2, '1+1', 'Тест', '2011-02-26', 112, 3);

-- Жанры фильмов
INSERT INTO "FilmGenre" ("FilmGenreId", "FilmId", "GenreId") VALUES (1, 1, 1);
INSERT INTO "FilmGenre" ("FilmGenreId", "FilmId", "GenreId") VALUES (2, 1, 2);
INSERT INTO "FilmGenre" ("FilmGenreId", "FilmId", "GenreId") VALUES (3, 1, 3);

-- Лайки
INSERT INTO "FilmLikes" ("FilmLikesId", "FilmId", "UserID") VALUES (1, 1, 1);