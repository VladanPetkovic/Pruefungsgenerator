//String insertStmt = "INSERT INTO study_programs (name, abbreviation) VALUES (?, ?);";
//
//String selectStmt = "SELECT * FROM study_programs;";
//String readStmt = "SELECT * FROM study_programs WHERE id = ?;";
//
//String readStmt = "SELECT * FROM study_programs WHERE name = ?;";
//
//String readStmt = "SELECT CASE " +
//        "WHEN COUNT(*) > 0 THEN 1 " +
//        "ELSE 0 " +
//        "END AS has_courses " +
//        "FROM has_sc " +
//        "WHERE fk_program_id = ?;";
//
//
//String updateStmt = "UPDATE study_programs SET name = ?, abbreviation = ? WHERE id = ?;";
//
//String deleteStmt = "DELETE FROM study_programs WHERE id = ?;";
//String deleteHasScStmt = "DELETE FROM has_sc WHERE fk_program_id = ?;";


// only leaving sql-statements for documentation and checking with springboot-generated statements