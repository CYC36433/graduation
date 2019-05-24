<template>
  <div class="app-container">
    <el-form class="user_info" :model="currentUser" :ref="currentUser" label-position="right" label-width="100px" inline>
      <el-form-item label="昵称：" prop="nickName">
        <label v-if="!updateflag">{{currentUser.nickName}}</label>
        <el-input v-else v-model="currentUser.nickName"></el-input>
      </el-form-item>
      <el-form-item label="性别：" prop="sex">
        <span v-if="!updateflag">{{currentUser.sex}}</span>
        <el-radio-group v-else v-model="currentUser.sex">
          <el-radio label="男"></el-radio>
          <el-radio label="女"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="年龄：" prop="age">
        <span v-if="!updateflag">{{currentUser.age}}</span>
        <el-input v-else v-model="currentUser.age"></el-input>
      </el-form-item>
      <el-form-item label="籍贯：" prop="native">
        <span v-if="!updateflag">{{currentUser.native}}</span>
        <el-input v-else v-model="currentUser.native"></el-input>
      </el-form-item>
      <el-form-item label="工作单位：" prop="workUnit">
        <span v-if="!updateflag">{{currentUser.workUnit}}</span>
        <el-input v-else v-model="currentUser.workUnit"></el-input>
      </el-form-item>
      <el-form-item label="职务：" prop="duty">
        <span v-if="!updateflag">{{currentUser.duty}}</span>
        <el-input v-else v-model="currentUser.duty"></el-input>
      </el-form-item>
      <el-form-item label="身份证号：" prop="IDNumber">
        <span v-if="!updateflag">{{currentUser.IDNumber}}</span>
        <el-input v-else v-model="currentUser.IDNumber"></el-input>
      </el-form-item>
      <el-form-item label="联系电话：" prop="phoneNumber">
        <span v-if="!updateflag">{{currentUser.phoneNumber}}</span>
        <el-input v-else v-model="currentUser.phoneNumber"></el-input>
      </el-form-item>
      <el-form-item label="Email：" prop="email">
        <span v-if="!updateflag">{{currentUser.email}}</span>
        <el-input v-else v-model="currentUser.email"></el-input>
      </el-form-item>
      <el-form-item class="handle">
        <div v-if="!updateflag">
        <el-button type="primary" @click="updateflag=true">编辑</el-button>
        <el-button type="danger" @click="logout" style="margin-left:50px;">注销</el-button></div>
        <div v-else>
        <el-button type="success" @click="updateInfo">保存</el-button>
        <el-button type="danger" @click="updateflag=false" style="margin-left:50px;">取消</el-button></div>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
  import {mapGetters} from 'vuex'
  export default {
    data() {
      return {
        rules: [

        ],
        totalCount: 0, //分页组件--数据总条数
        list: [],//表格的数据
        listLoading: false,//数据加载等待动画
        listQuery: {
          pageNum: 1,//页码
          pageRow: 50,//每页条数
        },
        roles: [],//角色列表
        dialogStatus: 'create',
        dialogFormVisible: false,
        textMap: {
          update: '编辑',
          create: '新建用户'
        },
        currentUser: {},
        updateflag: false,
        tempUser: {
          username: '',
          password: '',
          nickname: '',
          roleId: '',
          userId: ''
        }
      }
    },
    created() {
      this.getInfo();
      if (this.hasPerm('user:add') || this.hasPerm('user:update')) {
        this.getAllRoles();
      };
    },
    computed: {
      ...mapGetters([
        'userId'
      ])
    },
    methods: {
      getAllRoles() {
        this.api({
          url: "/user/getAllRoles",
          method: "get"
        }).then(data => {
          this.roles = data.list;
        })
      },
      getInfo() {
        //查询列表
        let vm = this;
        vm.api({
          url: '/login/getInfo',
          method: 'post'
        }).then(data=>{
          vm.currentUser = data.userPermission;
        })
      },
      updateInfo(){
        let vm = this;
        vm.currentUser.deleteStatus = '1';
        vm.$confirm("是否修改个人信息","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(()=>{
          vm.api({
            url: "/user/updateUser",
            method: "post",
            data: vm.currentUser
          }).then(()=>{
            vm.$message({
            type:"success",
            message:"修改成功！"
          })
          vm.getInfo();
          })
          vm.updateflag = false;
        })
      },
      logout(){
        this.$confirm("确定注销本会员信息？","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(()=>{
          this.currentUser.deleteStatus = '2';
          this.api({
            url: '/user/updateUser',
            method: 'post',
            data: this.currentUser
          }).then(()=>{
            this.$message.success("注销成功！即将返回登录界面");
            this.$store.dispatch('LogOut').then(() => {
              location.reload()
            })
          })
        })
      },
      handleSizeChange(val) {
        //改变每页数量
        this.listQuery.pageRow = val
        this.handleFilter();
      },
      handleCurrentChange(val) {
        //改变页码
        this.listQuery.pageNum = val
        this.getList();
      },
      handleFilter() {
        //查询事件
        this.listQuery.pageNum = 1
        this.getList()
      },
      getIndex($index) {
        //表格序号
        return (this.listQuery.pageNum - 1) * this.listQuery.pageRow + $index + 1
      },
      showCreate() {
        //显示新增对话框
        this.tempUser.username = "";
        this.tempUser.password = "";
        this.tempUser.nickname = "";
        this.tempUser.roleId = "";
        this.tempUser.userId = "";
        this.dialogStatus = "create"
        this.dialogFormVisible = true
      },
      showUpdate($index) {
        let user = this.list[$index];
        this.tempUser.username = user.username;
        this.tempUser.nickname = user.nickname;
        this.tempUser.roleId = user.roleId;
        this.tempUser.userId = user.userId;
        this.tempUser.deleteStatus = '1';
        this.tempUser.password = '';
        this.dialogStatus = "update"
        this.dialogFormVisible = true
      },
      createUser() {
        //添加新用户
        this.api({
          url: "/user/addUser",
          method: "post",
          data: this.tempUser
        }).then(() => {
          this.getList();
          this.dialogFormVisible = false
        })
      },
      updateUser() {
        //修改用户信息
        let _vue = this;
        this.api({
          url: "/user/updateUser",
          method: "post",
          data: this.tempUser
        }).then(() => {
          let msg = "修改成功";
          this.dialogFormVisible = false
          if (this.userId === this.tempUser.userId) {
            msg = '修改成功,部分信息重新登录后生效'
          }
          this.$message({
            message: msg,
            type: 'success',
            duration: 1 * 1000,
            onClose: () => {
              _vue.getList();
            }
          })
        })
      },
      removeUser($index) {
        let _vue = this;
        this.$confirm('确定删除此用户?', '提示', {
          confirmButtonText: '确定',
          showCancelButton: false,
          type: 'warning'
        }).then(() => {
          let user = _vue.list[$index];
          user.deleteStatus = '2';
          _vue.api({
            url: "/user/updateUser",
            method: "post",
            data: user
          }).then(() => {
            _vue.getList()
          }).catch(() => {
            _vue.$message.error("删除失败")
          })
        })
      },
    }
  }
</script>

<style>
.app-container .el-form--inline .el-form-item__content{
  width: 250px;
}
</style>

<style scoped>
.user_info{
  width:800px;
  margin-left:25%;
  margin-top:50px
}
.user_info .el-form-item{
  margin-top: 15px;
}
.handle{
  display: block;
  width: 50%;
  margin: auto;
}
</style>
