# 특정 user를 선택했을 때, 해당 user의 following 목록을 출력하기
-- 선택된 user의 id가 2일때
select u.user_id, u.user_name, u.name from user u where user_id in
(select following_id from following where user_id = 2);
select u.user_id, u.user_name, u.name from user u where user_id in
(select follower_id from follower where user_id = 2);


# 특정 user를 선택했을 때, 해당 user의 following과 follower의 수를 각각 출력하기
-- Following 수
select count(*) as following_count from following where user_id=2;
-- Follower 수
select count(*) as follower_count from follower where user_id=2;


# 입력받은 user_name과 password가 일치하면 해당 user의 user_id, user_name, name, following수, follower수를 출력하기
select u.user_id, u.user_name, u.name,
(select count(*) from following where user_id = u.user_id) as following_count,
(select count(*) from follower where user_id = u.user_id) as follower_count
from user u
where u.user_name = "nah25_01" and u.password = 5555;


# 회원가입
insert into user(user_name, password, name, email, birthday, gender)
select * from (select "lc_lc1m", "8282", "김건", "eeee@gachon.ac.kr", '2003-07-09', "male") as new_user
where not exists (select user_name from user where user_name = "lc_lc1m");
select * from user;


# following -> follower 반영
# 특정 user가 following을 하면 following 목록에 추가하고 following 대상의 follower 목록에도 추가
# following(user_id, following_id) follower(following_id, user_id) 서로 교차
insert into following values (5,2);
insert into follower values (2,5);

-- following, follower 수 출력
select count(*) as following_count from following where user_id=2;
select count(*) as follower_count from follower where user_id=2;


# following 취소 -> follower 취소 반영
# 특정 user가 following을 취소하면 following 목록에서 제거하고
# following 대상의 follower 목록에서 제외
delete from following where user_id = 5 and following_id = 2;
delete from follower where user_id = 2 and follower_id = 5;

-- following, follower 수 출력
select count(*) as following_count from following where user_id=2;
select count(*) as follower_count from follower where user_id=2;

# 내가 팔로우하지 않은 user 목록 조회
select u.user_id, u.user_name, u.name from user u where u.user_id not in
(select following_id from following where user_id = 1) and user_id <> 1 limit 5;


# 내가 팔로우하는 사람들 중, 내가 팔로우하지 않은 user를 팔로잉하는 사람들의 수 출력
-- 팔로우하지 않는 사용자들을 선택
with NonFollowing as
(select u.user_id, u.user_name, u.name from user u
where u.user_id <> 1 and u.user_id not in
(select following_id from following where user_id = 1))

-- 각 팔로우하지 않는 사용자들의 팔로워 중 사용자 ID가 1인 사용자가 팔로우중인 수를 출력
select u2.user_id, count(f.user_id) as follower_count from NonFollowing u2 left join following f on
u2.user_id = f.user_id and f.following_id = 1 group by u2.user_id;


# 내가 팔로우하지 않은 user를 조회하고, 해당 user와 내가 함께 아는 친구의 개수 조회
CREATE VIEW follower_count AS (
WITH NonFollow AS (
    SELECT u.user_id
    FROM user u 
    WHERE u.user_id <> 1 AND u.user_id NOT IN (SELECT following_id FROM following WHERE user_id = 1)
)
SELECT u2.user_id, COUNT(f.user_id) AS follower_count
FROM NonFollow u2
LEFT JOIN following f ON u2.user_id = f.user_id AND f.following_id = 1
GROUP BY u2.user_id
);

CREATE VIEW Nonfollow AS (
SELECT u.user_id, u.user_name, u.name
FROM user u 
WHERE u.user_id NOT IN (SELECT following_id FROM following WHERE user_id = 1) 
AND u.user_id <> 1
);

select * from Nonfollow natural join follower_count;


# 비밀번호 변경
update user set password = "1235"
where user_id = 1 and password = 1234;
