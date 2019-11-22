# logic-exec-engine

[![Join the chat at https://gitter.im/topicsys/logic-exec-engine](https://badges.gitter.im/topicsys/logic-exec-engine.svg)](https://gitter.im/topicsys/logic-exec-engine?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**Smart contract** and **generic logic** execution engine.

## 特性：

- 运行在 JVM 之上；
- 是个容器；
- 可同时加载多个实例；
- 每个实例可加载运行其它字节码；
- 利用 ASM 字节码工具可精确统计容器内加载字节码的运算量(相当于 **gasSize**)。

## 用途：
- 可用于用 `Scala DSL` 编写的智能合约的执行；
- 也可用于钱包重要代码的热混淆。

## 本`合约执行引擎`要实现以下几个能力：

- 编译插件。在编译完成之后，利用 ASM 工具插入 **gasSize** 统计代码；

- 合约的类库。一个用 DSL 实现的功能集合，智能合约唯一可 `import` 的类库，能够实现`组词造句`式的极简的合约代码编写范式，并限制合约的可操作功能范围；
  > 类库需要混淆压缩，以便提高执行性能。

- 合约文件的 IDE 环境适配。并不需要 `import`，编译时自动 `import` 库文件，并能够在常规 IDE 中正常显示。
