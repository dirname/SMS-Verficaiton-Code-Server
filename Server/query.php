<?php
/**
 * Created by PhpStorm.
 * User: Ah
 * Date: 2018/6/24
 * Time: 23:55
 */

require_once ('config.php'); //引用数据库配置
require_once ('encrypt.php'); //引用aes加密解密

$params = $_POST["data"];
$sign = $_POST["sign"];
if ($sign != md5($params."client!!!")){
    $array = [
        'status' => 400300,
        'message' => '参数校验错误',
        'time'=> time()
    ];
    echo json_encode($array);
}else {
    try {
        $data = json_decode(Aes::decrypt($params), true);
    } catch (Exception $e) {
        $array = [
            'status' => 400000,
            'message' => '非法提交 !',
            'time' => time()
        ];
        echo json_encode($array);
    }
    $phone = Aes::encrypt($data["phone"]);
    $code = $data["code"];
    if (empty($phone)) {
        $array = [
            'status' => 400200,
            'message' => '不合理的参数 !',
            'time' => time()
        ];
        echo json_encode($array);
    } else {
        //尝试连接数据库
        try {
            $conn = mysqli_connect($_config["mysql_server"], $_config["mysql_user"], $_config["mysql_password"], $_config["mysql_dbname"]);
        } catch (Exception $e) { //异常处理
            echo $e->getMessage();
        }
        if (mysqli_connect_errno($conn)) {
            $array = [
                'status' => 400100,
                'message' => '服务器错误 !',
                'time' => time()
            ];
            die(json_encode($array));
        } else{
            $sql = "SELECT * FROM `verifty` WHERE `phone` = '$phone' and `code` = '$code'";
            $result = mysqli_query($conn,$sql);
            if ($row = mysqli_fetch_array($result))
            {
                $veriftyed = $row["verifty"];
                $verifty_time = $row["verifty_time"];
                $phone = $row["phone"];
                $code = $row["code"];
                if ($veriftyed == 0){
                    $info = [
                        'verifty_time' => $verifty_time,
                        'phone' => $phone,
                        'code' => $code
                    ];
                    $array = [
                        'status' => 0,
                        'info' => $info,
                        'message' => '已认证 !',
                        'time' => time()
                    ];
                    echo json_encode($array);
                } else if ($veriftyed == 1){
                    $array = [
                        'status' => 400600,
                        'message' => '未认证 !',
                        'time' => time()
                    ];
                    echo json_encode($array);
                }
            }else{
                $array = [
                    'status' => 400400,
                    'message' => '没有找到匹配的信息 !',
                    'time' => time()
                ];
                echo json_encode($array);
            }
        }
        mysqli_close($conn);
    }
}