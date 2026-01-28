<template>
  <div class="product-edit">
    <el-card class="edit-card">
      <h2>{{ form.id ? '编辑商品' : '新增商品' }}</h2>

      <el-form :model="form" label-width="100px">

        <!-- 名称 -->
        <el-form-item label="商品名称" required>
          <el-input v-model="form.name" />
        </el-form-item>

        <!-- 分类 -->
        <el-form-item label="分类" required>
          <el-select
            v-model="form.categoryId"
            placeholder="请选择分类"
            style="width:240px"
          >
            <el-option
              v-for="c in categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>

          <el-button
            icon="Plus"
            type="primary"
            circle
            class="add-btn"
            @click="showAddCategory = true"
          />
        </el-form-item>

        <!-- 价格 -->
        <el-form-item label="价格(¥)" required>
          <el-input v-model="form.price" type="number" />
        </el-form-item>

        <!-- 库存 -->
        <el-form-item label="库存" required>
          <el-input v-model="form.stock" type="number" />
        </el-form-item>

        <!-- 单位 -->
        <el-form-item label="单位">
          <el-input v-model="form.unit" />
        </el-form-item>

        <!-- 商品介绍 -->
        <el-form-item label="商品介绍">
          <el-input type="textarea" v-model="form.description" rows="4" />
        </el-form-item>

        <!-- 上传 -->
        <el-form-item label="商品图片">
          <el-upload
            action="http://localhost:8080/api/upload"
            list-type="picture-card"
            :file-list="fileList"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <!-- 状态 -->
        <el-form-item label="状态">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            inline-prompt
            active-text="上架"
            inactive-text="下架"
          />
        </el-form-item>

        <!-- 按钮 -->
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
          <el-button @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 新增分类对话框 -->
    <el-dialog v-model="showAddCategory" title="新增分类" width="400px">
      <el-form :model="newCategory" label-width="80px">
        <el-form-item label="分类名称" required>
          <el-input v-model="newCategory.name" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddCategory = false">取消</el-button>
        <el-button type="primary" @click="saveNewCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { addProduct, updateProduct, getProductById } from "@/api/product"
import { getCategories, addCategory } from "@/api/category"
import { ElMessage } from "element-plus"
import { Plus } from "@element-plus/icons-vue"

const route = useRoute()
const router = useRouter()

const categories = ref([])

const form = ref({
  id: null,
  categoryId: null,
  name: "",
  price: 0,
  stock: 0,
  unit: "",
  cover: "",
  description: "",
  status: 1
})

const fileList = ref([])

// 加载分类
const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res.data
}

// 上传成功
const handleUploadSuccess = (res, file, listNow) => {
  file.url = res.data
  fileList.value = listNow
  form.value.cover = listNow[0]?.url || ""
}

// 删除图片
const handleRemove = (file, listNow) => {
  fileList.value = listNow
  form.value.cover = listNow[0]?.url || ""
}

// 保存商品
const save = async () => {
  if (!form.value.categoryId) {
    return ElMessage.error("分类不能为空")
  }

  const res = form.value.id
    ? await updateProduct(form.value)
    : await addProduct(form.value)

  if (res.code === 200) {
    ElMessage.success("保存成功")
    router.push("/products")
  }
}

// 新增分类
const showAddCategory = ref(false)
const newCategory = ref({ name: "" })

const saveNewCategory = async () => {
  if (!newCategory.value.name.trim()) {
    return ElMessage.warning("请输入分类名称")
  }
  await addCategory(newCategory.value)
  await loadCategories()
  showAddCategory.value = false
  newCategory.value.name = ""
}

// 初始化
onMounted(async () => {
  await loadCategories()

  // 如果是编辑模式
  if (route.params.id) {
    const res = await getProductById(route.params.id)
    if (res.code === 200) {
      form.value = res.data

      // 封面图片回显
      if (res.data.cover) {
        fileList.value = [{ name: "封面图", url: res.data.cover }]
      }
    }
  }
})
</script>

<style scoped>
.edit-card {
  max-width: 700px;
  margin: auto;
  padding: 20px 40px;
}
.add-btn {
  margin-left: 10px;
}
</style>
