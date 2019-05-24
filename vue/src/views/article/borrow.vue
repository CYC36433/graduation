<template>
  <div class="app-container">
    <div class="filter-container" style="margin-left:80px;margin-bottom:30px;padding: 0 10px;width:50%;height:60px">
      <el-input v-if="hasPerm('borrow:update')" placeholder="请输入借阅者姓名搜索" v-model="filter.borrowername" style="width:250px;margin:15px 0" size="small">
        <el-button slot="append" icon="el-icon-search"></el-button>
      </el-input>
      <el-select placeholder="图书类型" v-model="filter.returned" size="small" style="margin:15px 0 15px 30px">
        <el-option label="全部" value=""></el-option>
        <el-option label="已归还" value="已归还"></el-option>
        <el-option label="未归还" value="未归还"></el-option>
      </el-select>
    </div>
    <div class="borrowtable">
    <el-table :data="tabledata" v-loading.body="listLoading" element-loading-text="拼命加载中" stripe border fit highlight-current-row>
      <el-table-column align="center" label="序号" width="80">
        <template slot-scope="scope">
          <span v-text="getIndex(scope.$index)"></span>
        </template>
      </el-table-column>
      <el-table-column prop="book_id" align="center" label="图书编号" width="180"></el-table-column>
      <el-table-column prop="type_name" align="center" label="图书类别" width="180"></el-table-column>
      <el-table-column prop="book_name" align="center" label="图书名称"></el-table-column>
      <el-table-column prop="borrower_name" align="center" label="借阅人用户名" width="220"></el-table-column>
      <el-table-column prop="borrow_time" align="center" label="借阅时间" width="240"></el-table-column>
      <el-table-column prop="return_time" align="center" label="归还期限" width="240"></el-table-column>
      <el-table-column prop="returned" align="center" label="实际归还时间">
        <template slot-scope="scope">
          <el-tag :type="scope.row.returnFlag">{{scope.row.returned}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="hasPerm('borrow:update')" align="center" label="操作">
        <template slot-scope="scope">
          <el-button :disabled="scope.row.isreturned=='已归还'" @click="returnBook(scope.row)" type="success" size="small">确认归还</el-button>
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
import format from "@/utils/index";
export default {
  data(){
    return {
      borrowlist: [],
      listdata: [],
      pageNum: 1,
      listLoading: false,
      user: {},
      filter: {
        borrowername: '',
        returned: ''
      }
    }
  },
  created(){
    this.getData();
  },
  computed:{
    tabledata(){
      var vm = this;
      vm.listdata = vm.borrowlist.filter(o=>{
        var b1 = vm.filter.borrowername === "" || o.borrower_name.indexOf(vm.filter.borrowername) > -1;
        var b2 = vm.filter.returned === "" || o.isreturned == vm.filter.returned;
        return b1 && b2;
      })
      var result = vm.listdata.slice((vm.pageNum-1)*10,vm.pageNum*10);
      return result;
    },
    count(){
      var vm = this;
      return vm.listdata.length;
    }
  },
  methods: {
    getData(){
      var list = [];
      var today = new Date();
      this.listLoading = true;
      if(this.$store.state.user.role === '图书馆管理员'){
        this.user.borrowerName = '';
      }else{
        this.user.borrowerName = this.$store.state.user.nickname;
      }
      var a = this.api({
        url: '/book/listBorrowInfo',
        method: 'post',
        data: this.user
      }).then(data=>{
        this.listLoading = false;
        list = data.list;
      }
      )
      a.then(()=>{
        for(var i = 0;i<list.length;i++){
        let time = new Date(list[i].return_time)
        if(list[i].returned == null){
          list[i].isreturned = '未归还';
          if(today.getTime() > (time.getTime()+24*3600*1000)){
            list[i].returned = '已逾期';
            list[i].returnFlag = 'danger';
          }else if(today.getTime() > (time.getTime() - 2*24*3600*1000)){
            list[i].returned = '即将到期';
            list[i].returnFlag = 'warning';
          }else {
            list[i].returned = '未归还';
            list[i].returnFlag = 'primary';
          }
        }else{
          list[i].isreturned = '已归还';
          let _time = new Date(list[i].returned)
          if(_time.getTime() > time.getTime()){
            list[i].returnFlag = 'danger';
          }else {
            list[i].returnFlag = 'success';
          }
        }
      }
      this.borrowlist = list;
      })
    },
    handleCurrentChange(val){
      this.pageNum = val;
      this.getData();
    },
    returnBook(val){
      var today = new Date().format('yyyy-MM-dd');
      this.$confirm("是否归还","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(()=>{
          val.returned = today;
          this.api({
            url: "/book/updateBorrowInfo",
            method: "post",
            data: val
          }).then(()=>{
            this.$message({
              type:"success",
              message:"成功归还！"
            })
            this.getData();
          })
        })
    },
    getIndex(val){
      return (this.pageNum - 1)*10 + val + 1;
    }
  }
}
</script>

<style scoped>
.borrowtable{
  min-height: 650px;
}
.page {
  position: relative;
  margin-left: 35%;
}
</style>
