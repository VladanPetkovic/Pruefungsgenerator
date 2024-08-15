//        String insertStmt = "INSERT INTO courses (name, number, lector) VALUES (?, ?, ?);";
//        String selectStmt = "SELECT * FROM courses;";
//        String selectStmt =
//                "SELECT courses.* " +
//                "FROM courses " +
//                "JOIN has_sc ON courses.id = has_sc.fk_course_id " +
//                "JOIN study_programs ON has_sc.fk_program_id = study_programs.id " +
//                "WHERE has_sc.fk_program_id = ?;";
//        String readStmt = "SELECT * FROM courses WHERE id = ?;";
//        String readStmt = "SELECT * FROM courses WHERE name = ?;";
//        String readStmt = "SELECT * FROM courses WHERE number = ?;";
//        String readStmt = "SELECT CASE " +
//                        "WHEN COUNT(*) > 0 THEN 1 " +
//                        "ELSE 0 " +
//                        "END AS has_categories " +
//                        "FROM has_cc " +
//                        "WHERE fk_course_id = ?;";
//        String updateStmt =
//                "UPDATE courses " +
//                "SET name = ?, number = ?, lector = ? " +
//                "WHERE id = ?";
//        String deleteStmt = "DELETE FROM courses WHERE id = ?;";
//        String deleteHasSCStmt = "DELETE FROM has_sc WHERE fk_course_id = ?;";
