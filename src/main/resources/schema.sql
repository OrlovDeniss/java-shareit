DROP TABLE IF EXISTS item_comments;
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS usr;

CREATE TABLE IF NOT EXISTS usr
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name        VARCHAR(100) NOT NULL,
    description TEXT         NOT NULL,
    available   BOOLEAN      NOT NULL,
    user_id     BIGINT       NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_user FOREIGN KEY (user_id) REFERENCES usr (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comment
(
    id      bigint GENERATED BY DEFAULT AS IDENTITY,
    text    VARCHAR(255)                NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id BIGINT                      NOT NULL,
    item_id BIGINT                      NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES usr (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_item FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS item_comments
(
    item_id     BIGINT NOT NULL,
    comments_id BIGINT NOT NULL,
    CONSTRAINT fk_item_comments_item_id FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_comments_comment_id FOREIGN KEY (comments_id) REFERENCES comment (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS booking
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT                      NOT NULL,
    user_id    BIGINT                      NOT NULL,
    status     VARCHAR                     NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES usr (id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_item FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE CASCADE
);