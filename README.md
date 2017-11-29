# MUA
Just a homework

## 更新日志

v0.0.6
`2011-11-29`
- fix: 部分情况[]出现的错误匹配
- feature: 初步添加对于函数的支持(可能还存在bug)
- feature: 添加对于函数多行输入的支持
- feature: 添加stop、output的支持
- optimize: 进一步完善数学运算，支持括号内加空格，表达式外部可以不加括号但是不能有空格
- note: 下一版本进一步对Mua.class中的函数进行分离
- note: 在后续某个版本中添加对于`**`即幂的支持
- note: 需要对输出进行优化,部分应该是`RuntimeError`而不是`SyntaxError`
- note: 四则运算中应该提示更完善的信息，有些时候只会提示`null`

v0.0.5
`2017-11-29`
- feature: 添加对+-*/%()数学运算的支持，支持括号内空格
- feature: 修改数学表达式的处理逻辑
- optimize: 逐步开始类的分离

v0.0.4
`2017-11-28`
- fix: 将自己写错了一直没发现，修改作者信息`st4rlgiht`为`st4rlight`
- fix: 修复左右括号匹配的处理bug
- optimize: 优化输出信息的格式
- feature: 添加repeate的支持

v0.0.3 
`2017-10-28`
- fix:修复gt命令无法识别的问题
- fix:修复gt、lt、eq在输入不同类型操作数时存在的数组溢出问题
- note:mod结果与python结果不一致，与java一致，考虑是否修改与python一致
- optimize: 优化输出提示信息

v0.0.2
`2017-10-27`
- feature: 修改解释器逻辑
- fix: 部分情况下出现的[]无法正确预格式化和处理的问题
- fix: 修复旧版解释器存在的前面定义后面调用仍为旧值的情况
- optimize: 优化输出提示信息

v0.0.1
`2017-10-26`
- feature: 第一个正式版本,实现所有的基本功能
- fix: 修复处理[]时存在的逻辑错误

## 偷偷放下题目

### 基本数据类型value

数字number，单词word，列表list，布尔bool

* 数字的字面量以[0~9]或'-'开头，不区分整数，浮点数
* 单词的字面量以双引号"开头，不含空格，采用Unicode编码。在"后的任何内容，直到空格（包括空格、tab和回车）为止的字符都是这个单词的一部分，包括其中可能有的"和[]等符号
* 列表的字面量以方括号[]包含，其中的元素以空格分隔；元素可是任意类型；元素类型可不一致

### 基本操作

基本形式：操作名 参数

操作名是一个不含空格的词，与参数间以空格分隔。参数可以有多个，多个参数间以空格分隔。每个操作所需的参数数量是确定的，所以不需要括号或语句结束符号。有的操作有返回值，有的没有。

一个程序就是操作的序列。

基本操作有：

* `//`：注释
* `make <word> <value>`： 将value绑定到word上。基本操作的单词不能用做这里的word。绑定后的word称作名字，位于命名空间。
* `thing <word>`：返回word所绑定的值
* `:<word>`：与thing相同
* `erase <word>`：清除word所绑定的值
* `isname <word>`：返回word是否是一个名字，true/false
* `print <value>`：输出value
* `read`：返回一个从标准输入读取的数字或单词
* `readlinst`：返回一个从标准输入读取的一行，构成一个列表，行中每个以空格分隔的部分是list的一个元素
* 运算符operator
	* `add`, `sub`, `mul`, `div`, `mod`：`<operator> <number> <number>`
	* `eq`, `gt`, `lt`：`<operator> <number|word> <number|word>`
	* `and`, `or`：`<operator> <bool> <bool>`
	* `not`：`not <bool>`

* `repeat <number> <list>`：运行list中的代码number次

### 函数定义和调用

#### 定义

		make <word> [<list1> <list2>]
			word为函数名
			list1为参数列表
			list2为操作列表

#### 调用

		<functionName> <arglist>
			<functionName>为make中定义的函数名，不需要双引号"
			<arglist>是参数列表，<arglist>中的值和函数定义时的<list1>中名字进行一一对应绑定

#### 函数相关的操作
			
* `output <value>`：设定value为返回给调用者的值，但是不停止执行
* `stop`：停止执行

### 表达式计算

允许使用以下运算符对数字进行计算：

	+-*/%()
	