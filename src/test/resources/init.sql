-- 1️⃣ Member 테이블 초기 데이터
INSERT INTO member (id, email, password, tel, nickname, role, type, public_id, status)
VALUES
    (1, 'user1@example.com', '$2a$10$NZnyCSwiBKHfm5HE/XeVsu2I6772X/e/mmqpLI/m.uhc4oJ34e8da', '01012345678', 'UserOne', 'USER', 'LOCAL', UUID_TO_BIN(UUID()), 'ACTIVE'),
    (2, 'user2@example.com', '$2a$10$wBxWQ5fvX5ZB/J7.f3zja.EoI8n0s24Z3jEEgn1WNcd7znPPrHgee', '01087654321', 'UserTwo', 'ADMIN', 'LOCAL', UUID_TO_BIN(UUID()), 'ACTIVE');

-- 2️⃣ ChatRoom 테이블 초기 데이터
INSERT INTO chat_room (room_id, name)
VALUES
    (1, 'General Chat'),
    (2, 'Sports Talk');

-- 3️⃣ FilterData 테이블 초기 데이터
INSERT INTO filter_data (id, word)
VALUES
    (1, 'badword1'),
    (2, 'badword2');

-- 4️⃣ Category 테이블 초기 데이터 (가정)
INSERT INTO category (id, name)
VALUES
    (1, 'soccer'),
    (2, 'e-sports');

-- 5️⃣ Board 테이블 초기 데이터
INSERT INTO p_board (id, member_id, category_id, title, content, link, created_ip, created_at, updated_at)
VALUES
    (1, 1, 1, 'First Board Title', 'First Board Content', 'http://example.com', '192.168.0.1', NOW(), NOW()),
    (2, 2, 2, 'Second Board Title', 'Second Board Content', 'http://example.net', '192.168.0.2', NOW(), NOW());

-- 6️⃣ BoardCount 테이블 초기 데이터 (가정)
INSERT INTO board_count (id, board_id, view_count, like_count)
VALUES
    (1, 1, 10, 2),
    (2, 2, 5, 1);

-- 7️⃣ News 테이블 초기 데이터
INSERT INTO p_news (id, category, title, thumb_img, created_at, updated_at)
VALUES
    (1, 'BASEBALL', 'Baseball News Title', 'http://example.com/image1.jpg', NOW(), NOW()),
    (2, 'ESPORTS', 'E-Sports News Title', 'http://example.com/image2.jpg', NOW(), NOW());

-- 8️⃣ NewsComment 테이블 초기 데이터
INSERT INTO p_news_comment (id, news_id, member_id, comment, ip, created_at, updated_at)
VALUES
    (1, 1, 1, 'Great article!', '192.168.1.1', NOW(), NOW()),
    (2, 2, 2, 'Interesting topic!', '192.168.1.2', NOW(), NOW());
