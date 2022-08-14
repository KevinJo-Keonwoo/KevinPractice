package com.my.exception;

/*
Class 만들때 java.lang.Exception  --> 우클릭 -> source generate constructors from Superclass'

//11장

객체 복제 -> Object의 clone()
얕은 복제(thin clone) -> shallow copy
깊은 복제(deep clone) -> deep copy
Shallow copy 
복제가 된 이후에는 완전히 별개의 객체 ->   복제 후 기존 객체 변수내용을 바꿔도 복제된 객체에서 바뀌지 않음 
별개의 독립적인 개체는 아니지만, 같은 메모리를 참조할 수도 있음 

Deep copy 
배열까지 같이 복제 새로운 배열 메모리를 만듦 .

finalize 객체 소멸자
필요가 없어진 객체와의 연결을 확실히 끊어줌. 메모리누수
연결을 안정적으로 끊지않고 객체를 garbage collection에 넣게되면 메모리 leak이 발생함
필요없는 객체에 null값을 넣으면 gc로 넣겠다는 의미이고, gc에서 이 객체를 소각할때 finalize메서드를 자동호출함 -> 메모리에서 완전삭제
DB와의 연결을 완전하게 해제할 수 있음 
가급적 만들지 말기 

System)
exit 프로그램종료
정상 종료일경우 System.exit(0);
비정상 종료일경우 System.exit(0이외의 다른 값);

gc 쓰레기 수집기 garbage collection 
gc일처리를 해달라고 JVM에 요청하는것, gc가 직접 일을 하는 것이아니라 요청하는 것 

currentTimeMillis
현재시각을 밀리세컨드,나노세컨드 단위로 돌려줌
주로 프로그램 실행소요시간 측정에 사용됨 

attribute - 속성
property - 속성

Class class 
리플렉션 - 객체의 정보를 알려주는 클래스 

m("Circle"); m("Product"); m("Object");
void m (Object o) {
o의 자료형 확인 
Class c = o.getClass();
sysout(c.getName());  자료형의 클래스 이름이 나옴 
}
Method [] m = c.getMethods(); //o의 메서드들  //java.lang.reflect
Field [] f = c.getFields(); //o가 가지고 있는 멤버필드들  //java.lang.reflect

for(int i = 0; i < m.length; i++) {
	Method method = m [i];
	if(method.getName().equals("test")) {       //가져온 이름이 test와 같은지 
		method.invoke(~);
	{
}

void m (String className){
Class c = Class.forName(className); //클래스 이름에 맞는 class파일 찾아 JVM에 로드 
Object o = c.newInstance(); //클래스타입의 객체 생성    Circle클래스의 객체 Object o를 생성, o는 Circle내의 Object부분만 참조 
}

메인메서드 실행 이후에 로드가 되는 클래스는 런타임클래스?  Circle클래스 Product클래스 
확장성이 더 높음 -> newinstance 클래스    String타입의 Circle, Product 등을 이용하여 다양한 타입의 객체를 생성
여기까지 리플렉션 

String
//기본형을 String으로 변환
String s = String.valueOf(i);  (o)
String s = (String)i; 		   (x)
문자열을 숫자로 바꾸기 
int i = Interger.parseInt("12");
double d = Double.parseDouble("12.3");  
boolean b = Boolean.parseBoolean("true");

str1 = new String("Hi");
str1.toUpperCase;				toUpperCase 문자열을 모두 대문자로 바꿈  
								final로 String이 선언되어있기 때문에 Hi의 본질이 대문자로 바뀌는것이 아님
								toUpperCase를 선언할 때 새로운 메모리번지를 만드는 것 
sysout(str1.toString()); //Hi   본질이 바뀐것은 아니기 때문에 그대로 Hi가 출력 

str2 = new String("Hi");
str2 = str2.toUpperCase;	    UpperCase한 것을 바로 Str2에 담아버렸음
sysout(str2); //HI

String str1 = new String("가나");		String타입의 객체를 만든것
String str2 = new String("가나");		String타입의 객체를 만든것
String str3 = "가나"					String타입의 객체를 만든것
String str4 = "가나"					String타입의 객체를 만든것 str4는 str3과 같은 메모리번지를 참조 
sysout(str1 == str2); //false
sysout(str1 == str3); //false
sysout(str3 == str4); //true
sysout(str1.equals(str2)); //true
sysout(str1.equals(str3)); //true
sysout(str3.equals(str4)); //true

string에는 equals 매서드 재정의 되어있음
  		   메모리 불변 --> 메모리를 항상 새로 만듦. 다양한 메서드가 제공되고 사용법이 편리
StringBuilder에는 equals 매서드 재정의 안되어 있기때문에 Object에서의 equals를 가져옴 
		   메모리를 효율적으로 쓸 수 있음 --> 메모리를 가변적으로 늘려쓸 수 있음. 사용이 불편함 

sb1 = new StringBuilder("가나");
sb2 = new StringBuilder("가나");
sysout(sb1==sb2); //false
sysout(sb1.equals(sb2)); //false   equals가 내용을 비교하는 것으로 재정의되어있지 않음 
sb1 = "가나" // 불가능함 직접넣는것 
sb1.append("다라") 기존 "가나"에 "다라"가 추가됨 

getclass() 

 */

public class AddException extends Exception {
	public AddException() {			//추가-> add    검색-> find   삭제-> remove 수정 ->modify 
		super();
	}

	public AddException(String message) {
		super(message);  //super로 detail message를 호출   --> 제공자가 제공한 메시지 호출하기 위해 
	}

	
	
	
	
}
