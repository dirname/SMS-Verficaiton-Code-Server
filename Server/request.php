<?php
/**
 * Created by PhpStorm.
 * User: Ah
 * Date: 2018/6/24
 * Time: 16:55
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
    try{
        $data = json_decode(Aes::decrypt($params),true);
    }catch (Exception $e){
        $array = [
            'status' => 400000,
            'message' => '非法提交 !',
            'time'=> time()
        ];
        echo json_encode($array);
    }

    $phone = Aes::encrypt($data["phone"]);
    if (empty($phone)){
        $array = [
            'status' => 400200,
            'message' => '不合理的参数 !',
            'time'=> time()
        ];
        echo json_encode($array);
    }else {
        //尝试连接数据库
        try {
            $conn = mysqli_connect($_config["mysql_server"],$_config["mysql_user"],$_config["mysql_password"],$_config["mysql_dbname"]);
        } catch (Exception $e) { //异常处理
            echo $e->getMessage();
        }
        if (mysqli_connect_errno($conn))
        {
            $array = [
                'status' => 400100,
                'message' => '服务器错误 !',
                'time'=> time()
            ];
            die(json_encode($array));
        } else {
            $deadline = $data["time"] + 1800; //30分钟内有效
            $md5 = md5($phone.time());
            $code = substr($md5,5,2).substr($md5,16,2).substr($md5,30,2);
            $sql = "SELECT * FROM `verifty` WHERE `code` = '$code'";
            $result = mysqli_query($conn,$sql);
            $num = mysqli_num_rows($result);
            if ($num == 0){
                $sql = "INSERT INTO `verifty` (`phone`,`code`,`deadline`) VALUES ('$phone','$code',FROM_UNIXTIME($deadline))";
                $result = mysqli_query($conn,$sql);
                if ($result){
                    $codeinfo =  [
                        'phone' => $phone,
                        'code' => $code,
                        'server' => $_config["server_number"],
                        'deadline' => $deadline
                    ];
                    $array = [
                        'status' => 0,
                        'message' => '成功',
                        'info' => $codeinfo,
                        'time'=> time()
                    ];
                    echo json_encode($array);
                } else {
                    $array = [
                        'status' => 400100,
                        'message' => '服务器错误 !',
                        'time'=> time()
                    ];
                    echo json_encode($array);
                }
            }else {
                $array = [
                    'status' => 400400,
                    'message' => '未知错误,请重新请求',
                    'time'=> time()
                ];
                echo json_encode($array);
            }
        }
        mysqli_close($conn);
    }
}

