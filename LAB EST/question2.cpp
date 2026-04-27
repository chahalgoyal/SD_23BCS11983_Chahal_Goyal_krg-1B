class Social_media_app{
    void call();
    void chat();
    void post();
};
class Whatsapp extends Social_media_app{
    @override
    void call();
    void chat();
    void post(){
        return not supported;
    }
};
class Facebook extends Social_media_app{
    @override
    void call();
    void chat();
    void post();
};

//ambiguity of Liskov's principle 

class Post_Able_App{
    void post();
};
class Callable_and_Chatable_App{
    void call();
    void chat();
};
class Whatsapp extends Callable_and_Chatable_App{
    @override
    void call(){}
    void chat(){}
};
class Facebook extends Callable_and_Chatable_App, Post_Able_App{
    @override
    void call(){}
    void chat(){}
    void post(){}
};
