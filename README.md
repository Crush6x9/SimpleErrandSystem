## 目录

```bash
# 前端
所有文件均在/frontend下

# 后端
所有文件均在/backend下

#实验报告
在/report下，包含汇报PPT、设计图例等

# 环境


# Github仓库
.gitignore控制的是 整个 Git 仓库的忽略规则，不部署子模块的话必须放在根目录下

# README.md
即本文件 作为项目说明会在Github的repositories页显示
```

## 首次配置

```bash
# 克隆
接受邀请后启动cmd
在你想要储存项目的目录下输入：

git clone https://github.com/Crush6x9/SimpleErrandSystem.git

这条命令会在当前目录下自动新建一个叫 SimpleErrandSystem 的文件夹，并把整个项目拉下来

```

## 更新！这一步非常重要，避免冲突！

```bash
# 最重要的一点：所有 git 命令必须在 SimpleErrandSystem 项目根目录下执行（就是 frontend 和 backend 的父目录）

# 每次开始写代码之前
在cmd的项目根目录下输入：

git pull origin main

拉取最新代码，确保你是基于最新版本修改的

# 每次更新完代码之后
启动cmd
在项目根目录下依次运行以下三条命令：

git add .
git commit -m "你的提交说明"
git push origin main

第一条命令add .只会上传所有你修改过的文件，所以不必担心缓存问题
```

## 
