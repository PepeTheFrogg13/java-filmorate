CREATE TABLE IF NOT EXISTS "User" (
    "UserID" INTEGER   NOT NULL AUTO_INCREMENT,
    "Email" VARCHAR(200)   NOT NULL,
    "Login" VARCHAR(200)   NOT NULL,
    "Name" VARCHAR(200),
    "Birthday" Date   NOT NULL,
    CONSTRAINT "pk_User" PRIMARY KEY (
                                         "UserID"
                                     )
    );

CREATE TABLE IF NOT EXISTS "FriendStatus" (
                                              "StatusId" INTEGER   NOT NULL AUTO_INCREMENT,
                                              "Name" VARCHAR(200)   NOT NULL,
    CONSTRAINT "pk_FrinedStatus" PRIMARY KEY (
                                                 "StatusId"
                                             )
    );

CREATE TABLE IF NOT EXISTS "UserFriends" (
                                             "UserFriendsId" INTEGER   NOT NULL AUTO_INCREMENT,
                                             "UserSenderId" int   NOT NULL,
                                             "UserRecipientId" int   NOT NULL,
                                             "StatusId" int   NOT NULL,
                                             CONSTRAINT "pk_UserFriends" PRIMARY KEY (
                                             "UserFriendsId"
)
    );

CREATE TABLE IF NOT EXISTS "Film" (
                                      "FilmId" INTEGER   NOT NULL AUTO_INCREMENT,
                                      "Name" VARCHAR(200)   NOT NULL,
    "Description" VARCHAR(200)   NOT NULL,
    "ReleaseDate" Date   NOT NULL,
    "Duration" INTEGER   NOT NULL,
    "RatingId" INTEGER,   --NOT NULL,
    CONSTRAINT "pk_Film" PRIMARY KEY (
                                         "FilmId"
                                     )
    );

CREATE TABLE IF NOT EXISTS "Genre" (
                                       "GenreId" INTEGER   NOT NULL AUTO_INCREMENT,
                                       "Name" VARCHAR(200)   NOT NULL,
    CONSTRAINT "pk_Genre" PRIMARY KEY (
                                          "GenreId"
                                      )
    );

CREATE TABLE IF NOT EXISTS "Rating" (
                                        "RatingId" INTEGER   NOT NULL AUTO_INCREMENT,
                                        "Name" VARCHAR(200)   NOT NULL,
    CONSTRAINT "pk_Rating" PRIMARY KEY (
                                           "RatingId"
                                       )
    );

CREATE TABLE IF NOT EXISTS "FilmGenre" (
                                           "FilmGenreId" INTEGER   NOT NULL AUTO_INCREMENT,
                                           "FilmId" INTEGER   NOT NULL,
                                           "GenreId" INTEGER   NOT NULL,
                                           CONSTRAINT "pk_FilmGenre" PRIMARY KEY (
                                           "FilmGenreId"
)
    );

CREATE TABLE IF NOT EXISTS "FilmLikes" (
                                           "FilmLikesId" INTEGER   NOT NULL AUTO_INCREMENT,
                                           "FilmId" INTEGER   NOT NULL,
                                           "UserID" INTEGER   NOT NULL,
                                           CONSTRAINT "pk_FilmLikes" PRIMARY KEY (
                                           "FilmLikesId"
)
    );

ALTER TABLE "UserFriends" ADD CONSTRAINT IF NOT EXISTS "fk_UserFriends_UserSenderId" FOREIGN KEY("UserSenderId")
    REFERENCES "User" ("UserID");

ALTER TABLE "UserFriends" ADD CONSTRAINT IF NOT EXISTS "fk_UserFriends_UserRecipientId" FOREIGN KEY("UserRecipientId")
    REFERENCES "User" ("UserID");

ALTER TABLE "UserFriends" ADD CONSTRAINT IF NOT EXISTS "fk_UserFriends_StatusId" FOREIGN KEY("StatusId")
    REFERENCES "FriendStatus" ("StatusId");

ALTER TABLE "Film" ADD CONSTRAINT IF NOT EXISTS "fk_Film_RatingId" FOREIGN KEY("RatingId")
    REFERENCES "Rating" ("RatingId");

ALTER TABLE "FilmGenre" ADD CONSTRAINT IF NOT EXISTS "fk_FilmGenre_FilmId" FOREIGN KEY("FilmId")
    REFERENCES "Film" ("FilmId");

ALTER TABLE "FilmGenre" ADD CONSTRAINT IF NOT EXISTS "fk_FilmGenre_GenreId" FOREIGN KEY("GenreId")
    REFERENCES "Genre" ("GenreId");

ALTER TABLE "FilmLikes" ADD CONSTRAINT IF NOT EXISTS "fk_FilmLikes_FilmId" FOREIGN KEY("FilmId")
    REFERENCES "Film" ("FilmId");

ALTER TABLE "FilmLikes" ADD CONSTRAINT IF NOT EXISTS "fk_FilmLikes_UserID" FOREIGN KEY("UserID")
    REFERENCES "User" ("UserID");
