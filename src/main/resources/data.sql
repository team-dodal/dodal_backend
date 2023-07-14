INSERT INTO category (name, category_value, emoji, sub_name)
SELECT
    name, category_value, emoji, sub_name
FROM
(
    SELECT '건강' as name, '001' as category_value, '💪' as emoji, '불끈불끈' as sub_name FROM DUAL UNION ALL
    SELECT '스터디' as name, '002' as category_value, '📖' as emoji, '으쌰으쌰 불타는' as sub_name FROM DUAL UNION ALL
    SELECT '취미' as name, '003' as category_value, '🖼' as emoji, '삶의 즐거움' as sub_name FROM DUAL UNION ALL
    SELECT '루틴' as name, '004' as category_value, '💫' as emoji, '지키면 뿌듯한' as sub_name FROM DUAL UNION ALL
    SELECT '기타' as name, '005' as category_value, '🖼' as emoji, '' as sub_name FROM DUAL
) A
WHERE NOT EXISTS (SELECT 1 FROM category)
;

INSERT INTO hash_tag (category_id, name)
SELECT
    category_id, name
FROM
    (
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '몸 짱짱맨 되기' as name FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '하나만 더' as name FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM hash_tag where category_id = (SELECT category_id from category where category_value = '001'))
;

INSERT INTO hash_tag (category_id, name)
SELECT
    category_id, name
FROM
    (
        SELECT (SELECT category_id from category where category_value = '002') as category_id, '불타는 의지' as name FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '002') as category_id, '꼭 하고 만다' as name FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM hash_tag where category_id = (SELECT category_id from category where category_value = '002'))
;

INSERT INTO hash_tag (category_id, name)
SELECT
    category_id, name
FROM
    (
        SELECT (SELECT category_id from category where category_value = '003') as category_id, '인생의 재미' as name FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '003') as category_id, '취미 찾기' as name FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM hash_tag where category_id = (SELECT category_id from category where category_value = '003'))
;

INSERT INTO hash_tag (category_id, name)
SELECT
    category_id, name
FROM
    (
        SELECT (SELECT category_id from category where category_value = '004') as category_id, '작은 것부터 차근차근' as name FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM hash_tag where category_id = (SELECT category_id from category where category_value = '004'))
;

INSERT INTO hash_tag (category_id, name)
SELECT
    category_id, name
FROM
    (
        SELECT (SELECT category_id from category where category_value = '005') as category_id, '뭐든지' as name FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '005') as category_id, '도전은 늘 아름다워요' as name FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM hash_tag where category_id = (SELECT category_id from category where category_value = '005'))
;


INSERT INTO tag (category_id, name, tag_value)
SELECT
    category_id, name, tag_value
FROM
    (
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '체중 관리' as name, '001001' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '홈 트레이닝' as name, '001002' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '식습관' as name, '001003' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '피트니스 센터' as name, '001004' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '야외 활동' as name, '001005' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '명상' as name, '001006' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '001') as category_id, '기타' as name, '001007' as tag_value FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where category_value = '001'))
;

INSERT INTO tag (category_id, name, tag_value)
SELECT
    category_id, name, tag_value
FROM
    (
        SELECT (SELECT category_id from category where category_value = '002') as category_id, '커리어' as name, '002001' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '002') as category_id, '자기개발' as name, '002002' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '002') as category_id, '자격증' as name, '002003' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '002') as category_id, '외국어' as name, '002004' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '002') as category_id, '기타' as name, '002005' as tag_value FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where category_value = '002'))
;

INSERT INTO tag (category_id, name, tag_value)
SELECT
    category_id, name, tag_value
FROM
    (
        SELECT (SELECT category_id from category where category_value = '003') as category_id, '음악/악기' as name, '003001' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '003') as category_id, '독서' as name, '003002' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '003') as category_id, '미술' as name, '003003' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '003') as category_id, '전시/관람' as name, '003004' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '003') as category_id, '글쓰기' as name, '003005' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '003') as category_id, 'SNS 활동' as name, '003006' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '003') as category_id, '기타' as name, '003007' as tag_value FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where category_value = '003'))
;

INSERT INTO tag (category_id, name, tag_value)
SELECT
    category_id, name, tag_value
FROM
    (
        SELECT (SELECT category_id from category where category_value = '004') as category_id, '기상' as name, '004001' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '004') as category_id, '수면' as name, '004002' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '004') as category_id, '계획' as name, '004003' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '004') as category_id, '뷰티' as name, '004004' as tag_value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where category_value = '004') as category_id, '기타' as name, '004005' as tag_value FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where category_value = '004'))
;



INSERT INTO tag (category_id, name, tag_value)
SELECT
    (SELECT category_id from category where category_value = '005') as category_id, '기타' as name, '005001' as tag_value
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where category_value = '005'))
;
