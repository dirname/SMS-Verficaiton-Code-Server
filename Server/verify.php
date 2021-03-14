<?php
/**
 * Created by PhpStorm.
 * User: Ah
 * Date: 2018/6/24
 * Time: 23:51
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
                $deadline = strtotime($row["deadline"]);
                if (time() > $deadline){
                    $array = [
                        'status' => 400500,
                        'message' => 'code 已失效 !',
                        'time' => time()
                    ];
                    echo json_encode($array);
                } else{
                    $now = time();
                    $sql = "UPDATE `verifty` SET `verifty` = 0,`verifty_time` = FROM_UNIXTIME($now) WHERE `phone` = '$phone' and `code` = '$code'";
                    $result = mysqli_query($conn,$sql);
                    if ($result){
                        $array = [
                            'status' => 0,
                            'message' => 'OK !',
                            'time' => time()
                        ];
                        echo json_encode($array);
                    } else{
                        $array = [
                            'status' => 400700,
                            'message' => '不存在的请求 !',
                            'time'=> time()
                        ];
                        echo json_encode($array);
                    }
                }
            } else{
                $array = [
                    'status' => 400400,
                    'message' => '没有找到匹配的信息 !',
                    'time' => time()
                ];
                echo json_encode($array);
            }
        }
        mysqli_query($conn);
    }
}