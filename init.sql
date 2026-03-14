CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500),
    content_type VARCHAR(255),
    board_id BIGINT,
    gathering_id BIGINT
);

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(255),
    age INT,
    hobby VARCHAR(255),
    role VARCHAR(50),
    nickname VARCHAR(255),
    image_id BIGINT,
    refresh_token VARCHAR(500),
    FOREIGN KEY (image_id) REFERENCES image(id)
);

CREATE TABLE IF NOT EXISTS gathering (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content LONGTEXT,
    register_date DATETIME(6),
    user_id BIGINT,
    count INT DEFAULT 0,
    image_id BIGINT,
    category_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (image_id) REFERENCES image(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    accepted BOOLEAN DEFAULT FALSE,
    gathering_id BIGINT,
    user_id BIGINT,
    date DATETIME(6),
    FOREIGN KEY (gathering_id) REFERENCES gathering(id),
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);

CREATE TABLE IF NOT EXISTS meeting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    meeting_date DATETIME(6),
    end_date DATETIME(6),
    content LONGTEXT,
    user_id BIGINT,
    gathering_id BIGINT,
    image_id BIGINT,
    count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (gathering_id) REFERENCES gathering(id),
    FOREIGN KEY (image_id) REFERENCES image(id)
);

CREATE TABLE IF NOT EXISTS attend (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meeting_id BIGINT,
    user_id BIGINT,
    date DATETIME(6),
    FOREIGN KEY (meeting_id) REFERENCES meeting(id),
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);

CREATE TABLE IF NOT EXISTS board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    gathering_id BIGINT,
    title VARCHAR(255),
    description LONGTEXT,
    register_date DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (gathering_id) REFERENCES gathering(id)
);

ALTER TABLE image ADD FOREIGN KEY (board_id) REFERENCES board(id);
ALTER TABLE image ADD FOREIGN KEY (gathering_id) REFERENCES gathering(id);

CREATE TABLE IF NOT EXISTS `likes` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    gathering_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (gathering_id) REFERENCES gathering(id)
);

CREATE TABLE IF NOT EXISTS alarm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(500),
    date DATETIME(6),
    checked BOOLEAN DEFAULT FALSE,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);

CREATE TABLE IF NOT EXISTS certification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    certification VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS recommend (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gathering_id BIGINT,
    date DATE,
    FOREIGN KEY (gathering_id) REFERENCES gathering(id)
);

CREATE TABLE IF NOT EXISTS fail (
    id BIGINT PRIMARY KEY,
    content VARCHAR(500),
    client_id VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS chat_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    user_id BIGINT,
    gathering_id BIGINT,
    count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (gathering_id) REFERENCES gathering(id)
);

CREATE TABLE IF NOT EXISTS chat_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    chat_room_id BIGINT,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id)
);

CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY,
    content VARCHAR(1000),
    chat_room_id BIGINT,
    chat_participant_id BIGINT,
    created_at DATETIME(6),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id),
    FOREIGN KEY (chat_participant_id) REFERENCES chat_participant(id)
);

CREATE TABLE IF NOT EXISTS read_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status BOOLEAN DEFAULT FALSE,
    chat_participant_id BIGINT,
    chat_message_id BIGINT,
    FOREIGN KEY (chat_participant_id) REFERENCES chat_participant(id),
    FOREIGN KEY (chat_message_id) REFERENCES chat_message(id)
);

CREATE TABLE IF NOT EXISTS outbox (
    outbox_id BIGINT PRIMARY KEY,
    event_type VARCHAR(50),
    payload TEXT,
    created_at DATETIME(6),
    processed BOOLEAN DEFAULT FALSE
);
