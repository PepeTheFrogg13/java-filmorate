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




