function commonResultHandle(result){
    if(!result){
        return false;
    }
    if(result.code!==0){
        alert(result.msg);
        return false;
    }
    return true;
}