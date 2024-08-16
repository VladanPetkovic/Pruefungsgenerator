# How to create the ER-diagram

[<kbd>&larr; Go Back</kbd>](er_diagram.md)

1. Visit https://dbdiagram.io/d
2. Insert the code below and generate a pdf (you will have the relations of the table visible)
3. Remember: When updating the database, update the code below accordingly
````
Table "categories" {
  "id" INTEGER [pk]
  "name" TEXT [not null]
}

Table "courses" {
  "id" INTEGER [pk]
  "name" TEXT [not null]
  "number" INTEGER [not null]
  "lector" TEXT [not null]
}

Table "study_programs" {
  "id" INTEGER [pk]
  "name" TEXT [not null]
  "abbreviation" TEXT [not null]
}

Table "images" {
  "id" INTEGER [pk]
  "image" BLOB [not null]
  "name" TEXT
  "position" INTEGER [not null]
  "comment" TEXT
  "fk_question_id" INTEGER [not null]
}

Table "keywords" {
  "id" INTEGER [pk]
  "fk_course_id" INTEGER [not null]
  "keyword" TEXT [not null]
}

Table "questions" {
  "id" INTEGER [pk]
  "fk_category_id" INTEGER [not null]
  "difficulty" INTEGER [not null]
  "points" FLOAT [not null]
  "question" TEXT [not null]
  "type" TEXT [not null]
  "remark" TEXT
  "created_at" TEXT
  "updated_at" TEXT
}

Table "answers" {
  "id" INTEGER [pk]
  "answer" TEXT [not null]
  "fk_question_id" INTEGER [not null]
}

Table "study_programs_courses" {
  "fk_program_id" INT
  "fk_course_id" INT

Indexes {
  (fk_program_id, fk_course_id) [pk]
}
}

Table "categories_courses" {
  "fk_course_id" INT
  "fk_category_id" INT

Indexes {
  (fk_course_id, fk_category_id) [pk]
}
}

Table "questions_keywords" {
  "fk_keyword_id" INT
  "fk_question_id" INT

Indexes {
  (fk_keyword_id, fk_question_id) [pk]
}
}

Ref:"categories"."id" < "questions"."fk_category_id"

Ref:"questions"."id" < "answers"."fk_question_id"

Ref:"study_programs"."id" < "study_programs_courses"."fk_program_id"

Ref:"courses"."id" < "study_programs_courses"."fk_course_id"

Ref:"courses"."id" < "keywords"."fk_course_id"

Ref:"courses"."id" < "categories_courses"."fk_course_id"

Ref:"categories"."id" < "categories_courses"."fk_category_id"

Ref:"questions"."id" < "images"."fk_question_id"

Ref:"keywords"."id" < "questions_keywords"."fk_keyword_id"

Ref:"questions"."id" < "questions_keywords"."fk_question_id"
````