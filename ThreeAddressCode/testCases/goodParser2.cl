class B {
    s : String <- "hi cool developers";
    g(y:String) : Int {
        y.concat(s)
    };
    f(x:Int) : Int {
        x*50
    };
};

class A {
    a : Int;
    b : B <- new B;
    f(x:Int) : Int {
        x+a
    };
};
class Main{

f(x:Int) : Int {
        x+a
    };

   main():Object{

       f(5)


   };
};