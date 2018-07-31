# logic-exec-engine
**Smart contract** and **generic logic** execution engine.


## 本`合约执行引擎`要实现以下几个能力：

- 编译插件。在编译完成之后，利用 ASM 工具插入 **gasSize** 统计代码；

- 合约的类库。一个用 DSL 实现的功能集合，智能合约唯一可 `import` 的类库，能够实现`组词造句`式的极简的合约代码编写范式，并限制合约的可操作功能范围；
  > 类库需要混淆压缩，以便提高执行性能。

- 合约文件的 IDE 环境适配。并不需要 `import`，编译时自动 `import` 库文件，并能够在常规 IDE 中正常显示。
