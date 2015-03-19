http://open.163.com/special/opencourse/ios7.html
video + 课件

## Lecture1

ios采用mvc模式：

Controllers can always talk directly to their Model.
Controllers can also talk directly to their View. by outlet
The Model and View should never speak to each other.

Can the View speak to its Controller?
Sort of. Communication is “blind” and structured.
The Controller can drop a target on itself.
Then hand out an action to the View.
The View sends the action when things happen in the UI.

Sometimes the View needs to synchronize with the Controller.
should will did
The Controller sets itself as the View’s delegate.
The delegate is set via a protocol (i.e. it’s “blind” to class).

Views do not own the data they display.
So, if needed, they have a protocol to acquire it. as datasource[data at, count]
Controllers are almost always that data source (not Model!).

Controllers interpret/format Model information for the View.

Can the Model talk directly to the Controller?
No. The Model is (should be) UI independent.
So what if the Model has information to update or something?
It uses a “radio station”-like broadcast mechanism.
Controllers (or other Model) “tune in” to interesting stuff.
A View might “tune in,” but probably not to a Model’s “station.”


### objective-c

objc 使用基于引用计数的垃圾回收算法，所以需要生命变量的引用计数强度：
strong means:
“keep the object that this property points to
in memory until I set this property to nil (zero)
(and it will stay in memory until everyone who has a strong pointer to it sets their property to nil too)”
weak would mean:
“if no one else has a strong pointer to this object, then you can throw it out of memory
and set this property to nil
(this can happen at any time)”


￼nonatomic means:
“access to this property is not thread-safe”.
We will always specify this for object pointers in this course. If you do not, then the compiler will generate locking code that will complicate your code elsewhere.

一个Card的例子：

Card.h

	//#import <Foundation/NSObject.h> 单独引用
	//#import <Foundation/Foundation.h> 基础包完整引用
	@import Foundation; // special syntax

	@interface Card : NSObject

	/*
	 * we use an @property which declares two methods: a “setter” and a “getter”
	 * This @property is a pointer to an object whose class is (or inherits from) NSString
	￼￼ * ALL objects live in the heap (i.e. are pointed to) in Objective-C, thus NSString *
	 */
	@property (strong, nonatomic) NSString *contents;

	/*
	 Primitive types are not stored in the heap, they do not need (strong\weak)
	 一般对bool会修改getter名为is开头，可以使用getter修饰符
	 */
	@property (nonatomic, getter=isChosen) BOOL chosen;
	@property (nonatomic, getter=isMatched) BOOL matched;

	/*
	 method declaration: name->match: with one Card argument and returns a int
	 */
	-(int) match:(Card *)card;

	@end

Card.m

	#import <Foundation/Foundation.h>
	#import "Card.h"

	// Private declarations
	@interface Card()

	@end

	@implementation Card

	/*
	 This is the @property implementation that the compiler generates automatically for you (behind the scenes).
	 
	 @synthesize is the line of code that actually creates the
	 backing instance variable that is set and gotten.
	 ￼
	@synthesize contents = _contents;

	- (NSString *)contents
	{
	  return _contents;
	}

	- (void)setContents:(NSString *)contents
	{
	  _contents = contents;
	}
	 */

	- (int) match:(Card *)card
	{
	    // square bracket to send message
	    if([card.contents isEqualToString:self.contents]) {
	        return 1;
	    }
	    return 0;
	}

	-(int) matchs:(NSArray *)cards
	{
	    // for loop
	    for(Card *card in cards) {
	        if([card.contents isEqualToString:self.contents]) {
	            return 1;
	        }
	    }
	    return 0;
	}

	@end


## Lecture2

### go on with objc

Deck.h

	@import Foundation;
	#import "Card.h"

	@interface Deck : NSObject

	/* Note that this method has 2 arguments (and returns nothing).
	 It’s called “addCard:atTop:”.￼￼
	 */
	- (void)addCard:(Card *)card atTop:(BOOL)attop;

	/* if we want an addCard: ￼￼￼method without atTop, define it separately
	 */
	- (void)addCard:(Card *)card;

	/*takes no arguments and returns a Card (i.e. a pointer to an instance  of a Card in the heap).
	 */
	- (Card *)drawRandomCard;
	@end

Deck.m

	#import <Foundation/Foundation.h>
	#import "Deck.h"

	@interface Deck()
	//A deck of cards needs private storage to keep
	@property (strong, nonatomic) NSMutableArray *cards;
	@end

	@implementation Deck

	-(NSMutableArray *)cards {
	    // lazy instantiation
	    if(!_cards) _cards = [[NSMutableArray alloc] init];
	    return _cards;
	}

	-(void) addCard:(Card *)card atTop:(BOOL)attop {
	    if(attop) {
	        [self.cards insertObject:card atIndex:0];
	    } else {
	        [self.cards addObject:card];
	    }
	}

	-(void) addCard:(Card *)card {
	    [self addCard:card atTop:NO];
	}

	/* drawRandomCard simply grabs a card from a
	 random spot in our cards array.
	 */
	-(Card *)drawRandomCard {
	    Card *card = nil;
	    
	    if(self.cards.count) {
	        unsigned index = arc4random() % self.cards.count;
	        // These square brackets actually are the equivalent of sending the message
	        // objectAtIndexedSubscript: to the array
	        card = self.cards[index];
	        [self.cards removeObjectAtIndex:index];
	    }
	    
	    return card;
	}

	@end





### Lecture 3 P115 +
































































































