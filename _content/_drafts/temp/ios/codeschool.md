https://www.codeschool.com/courses/try-objective-c

#### level 2 Sending messages and getting results


send message
	[tryobjc completeThisChallenge];

description is like toString

	NSArray *foods = @[@"tacos", @"burgers"];
	NSLog(@"%@", [foods description]);

	NSArray *foods = @[@"tacos", @"burgers"];             
	NSString *result = [foods description];
	NSLog(@"%@", result);

NSUInteger与别的对象不同，需要特殊语法【声明没有*,NSLog时使用 %lu】

	NSString *city = @"Ice World";
	NSUInteger cityLength = [city length];
	NSLog(@"City has %lu characters", cityLength);

##### Operating on NSNumbers

对象和数值的区别，一个* 难以置信的复杂

	NSNumber *higgiesAge = @6;                                                                   
	NSNumber *phoneLives = @3;
	
	NSUInteger higgiesAgeInt = [higgiesAge unsignedIntegerValue];
	NSUInteger phoneLivesInt = [phoneLives unsignedIntegerValue];
	
	NSUInteger higgiesRealAge = higgiesAgeInt * phoneLivesInt;
	NSLog(@"Higgie is actually %lu years old.", higgiesRealAge);

##### Appending 2 strings

	NSString *firstName = @"qgd";
	NSString *lastName = @"gy";
	
	// NSString *fullName = [firstName + lastName]; // not work
	NSString *fullName = [firstName stringByAppendingString:lastName];
	NSLog(@"%@", fullName);

加个空格

	NSString *fullName = [[firstName stringByAppendingString:@" "] stringByAppendingString:lastName];

多个参数：replace方法

	NSString *replaced = [fullName stringByReplacingOccurrencesOfString:firstName
	                                                         withString:lastName];

NSString 的方法

	NSString *emptyString = [NSString string];
	NSString *copy = [NSString stringWithString:firstName];

其它类的类似方法

	NSArray *emptyArray = [NSArray array];
	NSDictionary *emptyDict = [NSDictionary dictionary];
	
	// common pattern
	NSString *emptyString = [[NSString alloc] init];
	NSArray *emptyArray = [[NSArray alloc] init];
	NSDictionary *emptyDictionary = [[NSDictionary alloc] init];

since creating an empty object isn’t that useful, most classes implement more specific init methods that you can send to the result of alloc to create objects with data

eg.

	NSString *copy = [[NSString alloc] initWithString:otherString];
	
##### Refactoring string combination

	NSString *fullname = [[firstName stringByAppendingString:@" "] stringByAppendingString:lastName];
	NSString *fullname = [NSString stringWithFormat:@"%@ %@", firstName, lastName];

#### LEVEL 3 Control the flow

	[hat isEqualToString:@"Sombrero"]

Switch on enums 必须包含enum全部路径


	NSArray *funnyWords = @[@"Schadenfreude", @"Portmanteau", @"Penultimate"];
	
	for (NSString *word in funnyWords) {
	  NSLog(@"%@ is a funny word", word);
	}

	NSDictionary *funnyWords = @{
	  @"Schadenfreude": @"pleasure derived by someone from another person's misfortune.",
	  @"Portmanteau": @"consisting of or combining two or more separable aspects or qualities",
	  @"Penultimate": @"second to the last"
	};
	
	for (NSString *word in funnyWords){
	  NSString *definition = funnyWords[word];
	  NSLog(@"%@ is defined as %@", word, definition);
	}

Note: Fast Enumeration earns its name by being faster than a traditional c-style for loop because it limits message sending, which can be slow when enumerating a 1000+ item collection object.

##### Playing with code blocks

	void (^logMessage)(void) = ^{
	  NSLog(@"Hello from inside the block");
	};
	logMessage();

求和的一个block

	void (^sumNumbers)(NSUInteger, NSUInteger) = ^(NSUInteger num1, NSUInteger num2){
	  NSLog(@"The sum of the numbers is %lu", num1 + num2);
	};

Notice how on the left side of the assignment operator (=) the arguments are defined only with their types (NSUInteger, NSUInteger), while on the right they are defined with their types and names (NSUInteger num1, NSUInteger num2)

	void (^logCount)(NSArray *) = ^(NSArray *array){
	  NSLog(@"There are %lu objects in this array", [array count]);
	};
	
	logCount(@[@"Mr.", @"Higgie"]);
	logCount(@[@"Mr.", @"Jony", @"Ive", @"Higgie"]);

##### Enumerate with blocks

使用enumerateObjectsUsingBlock: 可以将代码块block传入enumerator中进行遍历，如下面两段是相同的：

	for (NSString *word in funnyWords) {
	  NSLog(@"%@ is a funny word", word);
	}
	
	[funnyWords enumerateObjectsUsingBlock:
	  ^(NSString *word, NSUInteger index, BOOL *stop){
	    NSLog(@"%@ is a funny word", word);  
	  }
	];
	
	// 或命名代码块
	void (^enumeratingBlock)(NSString *, NSUInteger, BOOL *) = 
	  ^(NSString *word, NSUInteger index, BOOL *stop){
	    NSLog(@"%@ is a funny word", word);
	  };
	                         
	[funnyWords enumerateObjectsUsingBlock:enumeratingBlock];


#### LEVEL 4 Create your own classes

基本类创建

	NSDictionary *talkingiPhone = @{
	  @"Name": @"Mr. Higgie", 
	  @"ModelNumber": @1
	};
	
	// TalkingiPhone.h
	@interface TalkingiPhone : NSObject
	
	@property NSString *phoneName;
	@property NSString *modelNumber;
	
	@end
	
	// TalkingiPhone.m
	#import "TalkingiPhone.h"
	
	@implementation TalkingiPhone
	@end
	
	// app
	#import "TalkingiPhone.h"
	
	TalkingiPhone *talkingiPhone = [[TalkingiPhone alloc] init];
	talkingiPhone.phoneName = @"my phone";
	NSLog(@"%@", talkingiPhone.phoneName);

Note: Properties, although accessed and set using dot notation, use message sending under the hood. When you write person.firstName, it actually calls [person firstName] and when you write person.firstName = @"Eric" it actually calls [person setFirstName:@"Eric"]

##### Creating a custom method

无参void方法

	//TalkingiPhone.h
	@interface TalkingiPhone : NSObject
	
	// list of properties
	
	-(void)speak;
	
	@end
	
	// TalkingiPhone.m
	#import "TalkingiPhone.h"
	
	@implementation TalkingiPhone
	
	- (void) speak;
	{
	  NSLog(@"Pouring coffee");
	}
	@end
	
	// app
	[talkingiPhone speak];

方法返回字符串：

	//TalkingiPhone.h
	@interface TalkingiPhone : NSObject
	
	@property NSString *phoneName;
	@property NSString *modelNumber;
	
	- (NSString *) speak;
	@end
	
	//TalkingiPhone.m
	#import "TalkingiPhone.h"
	
	@implementation TalkingiPhone
	-(NSString *)speak;
	{
	    NSString *message = [NSString stringWithFormat:@"%@ says Hello There!", self.phoneName];
	    return message;
	}
	@end

带参数的方法

	// TalkingiPhone.h
	@interface TalkingiPhone : NSObject
	
	@property NSString *phoneName;
	@property (readonly) NSString *modelNumber; // 可以使用(readonly)限制赋值操作
	
	-(NSString *)speak:(NSString *)greeting;
	@end
	
	// TalkingiPhone.m
	#import "TalkingiPhone.h"
	
	@implementation TalkingiPhone
	-(NSString *)speak:(NSString *)greeting;
	{
	  NSString *message = [NSString stringWithFormat:@"%@ says %@", self.phoneName, greeting];
	  
	  return message;
	}
	@end
	
	// app
	TalkingiPhone *talkingiPhone = [[TalkingiPhone alloc] init];
	talkingiPhone.phoneName = @"Mr. Higgie";
	[talkingiPhone speak:@"Hello There!"];
	

Note: Message names that take arguments should include the : in the name. So a message named speak takes no arguments, while one named speak: takes a single argument. A message takes the same number of arguments as :'s in its name. So with something like dictionaryWithObjects:forKeys:count: you know it takes 3 arguments.

decreaseBatteryLife
	
	- (void) decreaseBatteryLife;
	{
	  self.batteryLife = @([self.batteryLife intValue] - 1);
	}

Note: If you are curious, this piece of code: @([self.batteryLife intValue] - 1); results in a NSNumber * object that is 1 less than self.batteryLife. It does this using an Objective-C boxed expression literal.

http://clang.llvm.org/docs/ObjectiveCLiterals.html#boxed-expressions

使用构造函数

	// TalkingiPhone.h
	@interface TalkingiPhone : NSObject {
	  NSNumber *_batteryLife;
	}
	
	@property NSString *phoneName;
	@property NSString *modelNumber;
	
	- (void) decreaseBatteryLife:(NSNumber *)arg;
	- (NSString *) speak:(NSString *)greeting;
	- (void) reportBatteryLife;
	@end
	
	// TalkingiPhone.m
	#import "TalkingiPhone.h"
	
	@implementation TalkingiPhone
	
	- (TalkingiPhone *)init;
	{
	  _batteryLife = @100;
	  return [super init];
	}
	
	- (void) decreaseBatteryLife:(NSNumber *)arg;
	{
	  _batteryLife = @([_batteryLife intValue] - [arg intValue]);
	}
	
	- (void) reportBatteryLife;
	{
	    NSLog(@"Battery life is %@", _batteryLife);
	}
	
	- (NSString *)speak:(NSString *)greeting;
	{
	    NSString *message = [NSString stringWithFormat:@"%@ says %@", self.phoneName, greeting];
	    return message;
	}
	@end
	
	// App
	#import "TalkingiPhone.h"
	
	TalkingiPhone *talkingiPhone = [[TalkingiPhone alloc] init];
	talkingiPhone.phoneName = @"Mr. Higgie";
	
	[talkingiPhone decreaseBatteryLife:@5];
	[talkingiPhone reportBatteryLife];



#### LEVEL 5 Learning from mistakes

objc的方法调用，准确的说是message passing，是不做运行时检查的，所以要判断一个类有没有某一个方法，可以使用respondsToSelector 检查：

	if([talkingiPhone respondsToSelector:@selector(copyWithZone)]){
	  ...
	}

##### Adopt NSCopying

编译时检查

	Person <NSCopying> *person = [[Person alloc] init];
	Person *copy = [person copy];
	
	// Person.h
	@interface Person : NSObject <NSCopying>
	@end

	// Person.m
	@implementation Person
	- (Person *) copyWithZone:(NSZone *)zone;
	{
	    Person *personCopy = [[Person allocWithZone:zone] init];
	    return personCopy;
	}
	@end

##### nil

nil has a special and useful feature - you can send messages to it and instead of causing an error

	TalkingiPhone *talkingiPhone = [[TalkingiPhone alloc] init];
	
	if([talkingiPhone.phoneName isEqualToString:@"Mr. Higgie"]){
	  NSLog(@"phoneName is equal to Mr. Higgie");
	}else{
	  NSLog(@"phoneName is not equal to Mr. Higgie");
	}

这里即使phoneName没有设定，也不会有NPE错误抛出

##### new init

	// Person.h
	@interface Person : NSOBject
	- (Person *) initWithFirstName:(NSString *)firstName 
	                      lastName:(NSString *)lastName;
	                      
	@property NSString *firstName;
	@property NSString *lastName;
	@end
	
	// Person.m
	@implementation Person
	- (Person *) initWithFirstName:(NSString *)firstName 
	                      lastName:(NSString *)lastName;
	{
	   _firstName = firstName;
	   _lastName = lastName;
	   return [super init];
	}
	@end
	
	Person *person = [[Person alloc] initWithFirstName:@"Tim" 
	                                          lastName:@"Cook"];

##### Object class

	[[[someExistingObject class] alloc] init];

##### id type

	TalkingiPhone *talkingPhone = [[ChargableTalkingiPhone alloc] init];
	NSObject *person = [[Person alloc] init];
	id person = [[Person alloc] init];
	
	Person *person = [[Person alloc] init];
	person.firstName = @"Eric";
	NSLog(@"%@", person.firstName);
	
	id person = [[Person alloc] init];
	[person setFirstName:@"Eric"];
	NSLog(@"%@", [person firstName]);

##### Pointers













