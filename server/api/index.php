<?php
global $localhost_db, $username_db, $password_db, $database_wordoftheday_api;
header("Content-Type:application/json");
if ($c = new mysqli($localhost_db, $username_db, $password_db, $database_wordoftheday_api)) {
    $c->set_charset("utf8");
//GET data
    $year = date('Y');
    $month = date("m");
    $day = date("d");

    $today_date = "YEAR(`date`) = " . $year . " AND MONTH(`date`) = " . $month . " AND DAY(`date`) = " . $day;

    $sql = "SELECT * FROM wordoftheday_en WHERE ${today_date}";

    $response = null;

    $r = $c->query($sql);
    if ($r->num_rows == 1) {
        $row = $r->fetch_array();
        $response["date"] = $row["date"];
        $response["word"] = ucfirst($row["word"]);
        $response["definition"] = ucfirst($row["definition"]);
        $response["type"] = $row["type"];
        $response["phonetics"] = $row["phonetics"];
        $response["etymology"] = ucfirst($row["etymology"]);
        $response["source"] = $row["source"];
    } else {
        $response["date"] = "null";
        $response["word"] = "";
        $response["definition"] = "";
        $response["type"] = "";
        $response["phonetics"] = "";
        $response["etymology"] = "";
        $response["source"] = "";
    }

    echo json_encode($response);
} else {
    echo_null();
}

function echo_null()
{
    echo json_encode(null);
}

?>