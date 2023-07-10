INSERT INTO category (name, value)
SELECT
    name, value
FROM
(
    SELECT '건강 💪' as name, '001' as value FROM DUAL UNION ALL
    SELECT '스터디 📖' as name, '002' as value FROM DUAL UNION ALL
    SELECT '취미 🖼' as name, '003' as value FROM DUAL UNION ALL
    SELECT '루틴 💫' as name, '004' as value FROM DUAL UNION ALL
    SELECT '기타' as name, '005' as value FROM DUAL
) A
WHERE NOT EXISTS (SELECT 1 FROM category)
;

INSERT INTO tag (category_id, name, value)
SELECT
    category_id, name, value
FROM
    (
        SELECT (SELECT category_id from category where value = '001') as category_id, '체중 관리' as name, '001001' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '001') as category_id, '홈 트레이닝' as name, '001002' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '001') as category_id, '식습관' as name, '001003' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '001') as category_id, '피트니스 센터' as name, '001004' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '001') as category_id, '야외 활동' as name, '001005' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '001') as category_id, '명상' as name, '001006' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '001') as category_id, '기타' as name, '001007' as value FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where value = '001'))
;

INSERT INTO tag (category_id, name, value)
SELECT
    category_id, name, value
FROM
    (
        SELECT (SELECT category_id from category where value = '002') as category_id, '커리어' as name, '002001' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '002') as category_id, '자기개발' as name, '002002' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '002') as category_id, '자격증' as name, '002003' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '002') as category_id, '외국어' as name, '002004' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '002') as category_id, '기타' as name, '002005' as value FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where value = '002'))
;

INSERT INTO tag (category_id, name, value)
SELECT
    category_id, name, value
FROM
    (
        SELECT (SELECT category_id from category where value = '003') as category_id, '음악/악기' as name, '003001' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '003') as category_id, '독서' as name, '003002' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '003') as category_id, '미술' as name, '003003' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '003') as category_id, '전시/관람' as name, '003004' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '003') as category_id, '글쓰기' as name, '003005' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '003') as category_id, 'SNS 활동' as name, '003006' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '003') as category_id, '기타' as name, '003007' as value FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where value = '003'))
;

INSERT INTO tag (category_id, name, value)
SELECT
    category_id, name, value
FROM
    (
        SELECT (SELECT category_id from category where value = '004') as category_id, '기상' as name, '004001' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '004') as category_id, '수면' as name, '004002' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '004') as category_id, '계획' as name, '004003' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '004') as category_id, '뷰티' as name, '004004' as value FROM DUAL UNION ALL
        SELECT (SELECT category_id from category where value = '004') as category_id, '기타' as name, '004005' as value FROM DUAL
    ) A
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where value = '004'))
;



INSERT INTO tag (category_id, name, value)
SELECT
    (SELECT category_id from category where value = '005') as category_id, '기타' as name, '005001'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tag where category_id = (SELECT category_id from category where value = '005'))
;
