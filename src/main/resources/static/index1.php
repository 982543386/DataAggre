<?php
$name=$_GET['a'];
$abc=array();
$abc['name']=$name;
die(json_encode($abc));
?>