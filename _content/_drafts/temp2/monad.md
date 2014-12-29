monad.md

http://jiyinyiyong.github.io/monads-in-pictures/

### Functor
Functor 就是任何能用 fmap 操作的数据类型

最后一个例子: 你把一个函数应用到另一个函数时会发生什么?
  fmap (+3) (+1)
这是个函数:

结果就是又一个函数!
  > import Control.Applicative
> let foo = (+3) <$> (+2)
> foo 10
15
这就是函数复合! 就是说, f <$> g == f . g!


### Applicative

Just (+3) <*> Just 2 == Just 5


  > [(*2), (+3)] <*> [1, 2, 3]
[2, 4, 6, 4, 5, 6]

像flatmap

这里有一些是你能用 Applicative 做, 而无法用 Functor 做到的. 你怎么才能把需要两个参数的函数应用到两个封装的值上呢?
  > (+) <$> (Just 5)
Just (+5)
> Just (+5) <$> (Just 4)
ERROR ??? WHAT DOES THIS EVEN MEAN WHY IS THE FUNCTION WRAPPED IN A JUST
Applicative:
  > (+) <$> (Just 5)
Just (+5)
> Just (+5) <*> (Just 3)
Just 8

Applicative 把 Functor 推到了一边. “大腕儿用得起任意个参数的函数,” 他说. “用 <$> 和 <*> 武装之后, 我可以接受需要任何个未封装的值的函数. 然后我传进一些封装过的值, 再我就得到一个封装的值的输出! AHAHAHAHAH!”
  > (*) <$> Just 5 <*> Just 3
Just 15

### Monad

Monads add a new twist.
Functor 应用函数到封装过的值:

Applicative 应用封装过的函数到封装过的值:

Monads 应用会返回封装过的值的函数到封装过的值. Monad 有个 >>= (念做 “bind”) 来做这个.







