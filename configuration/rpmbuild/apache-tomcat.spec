# 软件包的名字 rpm 查询时的最少必要字段
Name:		apache-tomcat
Version:	8.5.37

# 会影响 版本之后，平台之前的rpm文件信息生成，
# 比如这里最终的rpm文件是 apache-tomcat-8.5.37-1.el7.x86_64.rpm
# 其中的 -1.el7 便是这个字段生成的
Release:	1%{?dist} 
Summary:	web server.	

Group:		Application/System	
License:	apache license	
URL:		tomcat.apache.com
Source0:	%{name}-%{version}.tar.gz

BuildRequires:	tar
Requires:	tar

# 定义软件为 relocatable 形式，
# 默认的安装路径是 apache，在file阶段通过安装命令组合成最终路径 /apache
# 可以通过 --prefix 或者是 --relocate /apache=/apache-new 来替换安装时的位置
# 注意：这个参数的路径需要以 / 开头，
# 否则使用 rpm 安装时 --prefix 选项会无效, --relocate 也会安装失败
Prefix:		/apache

%description	
apache tomcat web server

#在每个rpmbuild的阶段中，rpm都会切换到对应目录中，然后执行后续命令
#比如prep阶段将执行
# umask 022
# cd /home/rpmbuild/rpmbuild/BUILD
%prep
echo --------------------------------------
echo %{name}
echo %{version}
echo %{release}

# 注意源文件的应用是大写的。。。
echo %{SOURCE0}

# rpmbuild --showrc 可以参看rpmbuild的内置变量
# _builddir	%{_topdir}/BUILD
# _buildrootdir	%{_topdir}/BUILDROOT
echo %{_builddir}
echo %{_buildrootdir}

# 注意和     _buildrootdir 变量的区别
# rpmbuild在 buildroot     变量目录后面添加了构建的rpm包名信息，用来隔离不同的rpm工程构建
# buildroot	%{_buildrootdir}/%{name}-%{version}-%{release}.%{_arch}
echo %{buildroot}

echo %{prefix}
echo --------------------------------------

# setup是一个宏, 他将会首先清理工作目录，然后解压源码，针对tar.gz，其他类型可以手动处理
# 所以我们无需手动调用如下俩句命令,可以直接使用setup来完成这些工作
# setup还会完成后续阶段的目录切换和设置，如果在这个阶段不用这条指令，那么后续的每个阶段都要自己手动去改变相应的目录。o
# cd /home/rpmbuild/rpmbuild/BUILD
# rm -rf apache-tomcat-8.5.37
# /usr/bin/gzip -dc /home/rpmbuild/rpmbuild/SOURCES/apache-tomcat-8.5.37.tar.gz
# /usr/bin/tar -xf -
# STATUS=0
# '[' 0 -ne 0 ']'
# cd apache-tomcat-8.5.37
# /usr/bin/chmod -Rf a+rX,u+w,g-w,o-w .

#rm -rf %{_builddir}/*
#tar -zxf %{SOURCE0}
%setup -q 

# 没什么事做的阶段，可以不写任何东西
# 即下面的 cd apache-tomcat-8.5.37 ，都是setup 自动添加的,包括setup开始后面的每个阶段都会有这个动作
# 而 cd /home/rpmbuild/rpmbuild/BUILD 则是rpmbuild在每个阶段会切换到对应阶段的目录语句。
# umask 022
# cd /home/rpmbuild/rpmbuild/BUILD
# cd apache-tomcat-8.5.37
# exit 0
%build
# 如果有源码的编译任务可以在这里调用 configure 以及 make 命令


# umask 022
# cd /home/rpmbuild/rpmbuild/BUILD
# '[' /home/rpmbuild/rpmbuild/BUILDROOT/apache-tomcat-8.5.37-1.el7.x86_64 '!=' / ']'
# rm -rf /home/rpmbuild/rpmbuild/BUILDROOT/apache-tomcat-8.5.37-1.el7.x86_64
# dirname /home/rpmbuild/rpmbuild/BUILDROOT/apache-tomcat-8.5.37-1.el7.x86_64
# mkdir -p /home/rpmbuild/rpmbuild/BUILDROOT
# mkdir /home/rpmbuild/rpmbuild/BUILDROOT/apache-tomcat-8.5.37-1.el7.x86_64
# cd apache-tomcat-8.5.37
%install
#安装前rpmbuild会自动清理之前操作产生的文件故无需手动清理
#rm -rf %{buildroot}

# 拷贝到 buildroot 目录的文件，这里的文件是可能打包进 rpm 中的文件
# 拷贝时注意路径，如果路径是一个目录，那么整个目录都会拷贝过去，这样子会保留该目录本身，其新的父目录变为 prefix 
# 如果是 /* 结尾的路径，将会对 /* 的目录做遍历，然后将遍历结果逐个复制到目标目录中，这些被遍历复制的文件的新的父目录是 prefix
mkdir -p %{buildroot}/%{prefix}
cp -r %{_builddir}/%{name}-%{version}  %{buildroot}/%{prefix}

# umask 022
# cd /home/rpmbuild/rpmbuild/BUILD
# cd apache-tomcat-8.5.37
%clean 

# 注意命令执行时发生的转换,如果路径是目录，那么这个目录本身将会被直接删除,
# 而当在目录后面使用 /* 时 rpmbuild会遍历该目录下的文件或目录然后对其逐个调用 rm -rf 命令去删除
# rm -rf /home/rpmbuild/rpmbuild/BUILD/apache-tomcat-8.5.37
# rm -rf /home/rpmbuild/rpmbuild/BUILDROOT/apache-tomcat-8.5.37-1.el7.x86_64
rm -rf %{_builddir}/*
rm -rf %{_buildrootdir}/*

# 处理构建架构
# '[' '%{buildarch}' = noarch ']'
%files

# 指定默认的文件权限，(文件权限，用户名，组名，目录权限),
# 符号 - 代表默认值，文件是 0644 可执行文件 0755，用户 rpmbuild运行时的用户，组 rpmbuild运行时的用户的组
%defattr (-,-,-,-)

# 指定从 buildroot 目录中添加到rpm包中的文件，这里的文件是确切的 rpm 要包含的文件
/%{prefix}

# 卸载后调用的脚本
%postun
# 卸载时，rpmbuild会按照%file阶段记录的文件来删除，不认识的文件他会跳过去。
# 所以这里手动调用卸载后执行的脚本，来清理所有的应用文件。
# 要小心对配置文件的处理，这样子就会丢失掉配置文件。
rm -rf /%{prefix}/%{name}-%{version}

%changelog

