<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:40px">
    </div>
    <div class="table">
    <el-table :data="tabledata" v-loading.body="listLoading" element-loading-text="拼命加载中" fit highlight-current-row>
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form class="userInfo" label-position="right" inline>
            <el-form-item label="用户名：">
              <span>{{props.row.nickname}}</span>
            </el-form-item>
            <el-form-item label="性别：">
              <span>{{props.row.sex}}</span>
            </el-form-item>
            <el-form-item label="年龄：">
              <span>{{props.row.age}}</span>
            </el-form-item>
            <el-form-item label="籍贯：">
              <span>{{props.row.native}}</span>
            </el-form-item>
            <el-form-item label="工作单位：">
              <span>{{props.row.workUnit}}</span>
            </el-form-item>
            <el-form-item label="职务：">
              <span>{{props.row.duty}}</span>
            </el-form-item>
            <el-form-item label="身份证号：">
              <span>{{props.row.IDNumber}}</span>
            </el-form-item>
            <el-form-item label="联系电话：">
              <span>{{props.row.phoneNumber}}</span>
            </el-form-item>
            <el-form-item label="Email：">
              <span>{{props.row.email}}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column align="center" label="序号" width="80">
        <template slot-scope="scope">
          <span v-text="getIndex(scope.$index)"> </span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="昵称" prop="nickname" width="200"></el-table-column>
      <el-table-column align="center" label="用户名" prop="username" width="200"></el-table-column>
      <el-table-column align="center" label="角色" width="180">
        <template slot-scope="scope">
          <el-tag type="success" v-text="scope.row.roleName" v-if="scope.row.roleId===1"></el-tag>
          <el-tag type="primary" v-text="scope.row.roleName" v-else></el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="320"></el-table-column>
      <el-table-column align="center" label="最近修改时间" prop="updateTime" width="320"></el-table-column>
      <el-table-column align="center" label="管理" v-if="hasPerm('user:update')">
        <template slot-scope="scope">
          <el-button type="danger" icon="el-icon-delete" v-if="scope.row.roleId!=1" size="small"
                     @click="removeUser(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>
    <el-pagination
      @current-change="handleCurrentChange"
      :current-page.sync="pageNum"
      :total="count"
      :page-size="10"
      layout="total, prev, pager, next, jumper" class="page">
    </el-pagination>
  </div>
</template>
<script>
  import {mapGetters} from 'vuex'

  export default {
    data() {
      return {
        count: 0, //分页组件--数据总条数
        list: [],//表格的数据
        listLoading: false,//数据加载等待动画
        pageNum: 1
      }
    },
    created() {
      this.getList();
    },
    computed: {
      tabledata(){
        var result = this.list.slice((this.pageNum-1)*10,this.pageNum*10);
        return result;
      }
    },
    methods: {
      getList() {
        //查询列表
        this.listLoading = true;
        this.api({
          url: "/user/list",
          method: "get"
        }).then(data => {   
          this.listLoading = false;
          this.list = data.list;
          this.count = this.list.length;
        })
      },
      handleCurrentChange(val) {
        //改变页码
        this.pageNum = val;
        this.getList();
      },
      getIndex(val) {
        //表格序号
        return (this.pageNum - 1)*10 + val + 1;
      },
      removeUser(val) {
        let _vue = this;
        this.$confirm('确定删除此用户?', '提示', {
          confirmButtonText: '确定',
          showCancelButton: false,
          type: 'warning'
        }).then(() => {
          let user = val;
          user.deleteStatus = '2';
          _vue.api({
            url: "/user/updateUser",
            method: "post",
            data: user
          }).then(() => {
            _vue.$message.success("删除成功")
            _vue.getList()
          }).catch(() => {
            _vue.$message.error("删除失败")
          })
        })
      },
    }
  }
</script>

<style scoped>
.userInfo .el-form-item {
  margin-left: 40px;
  margin-bottom: 0;
  width: 40%;
}
.table{
  min-height: 650px;
}
.page {
  position: relative;
  margin-left: 35%;
}
</style>
