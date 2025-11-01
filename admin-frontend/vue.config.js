const { defineConfig } = require('@vue/cli-service')
const path = require('path')

module.exports = defineConfig({
  transpileDependencies: true,
  
  // 禁用 ESLint
  lintOnSave: false,
  
  // 开发服务器配置
  devServer: {
    port: 5173,
    open: true,
    proxy: {
      '/admin': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
    }
  },

  // 路径别名
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      },
      extensions: ['.ts', '.tsx', '.js', '.jsx', '.vue', '.json']
    },
    // 入口文件配置
    entry: {
      app: './src/main.ts'
    },
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          loader: 'ts-loader',
          exclude: /node_modules/,
          options: {
            appendTsSuffixTo: [/\.vue$/],
            transpileOnly: true
          }
        }
      ]
    }
  },

  // 生产环境配置
  productionSourceMap: false,

  // 链式配置
  chainWebpack: config => {
    // 修改 HTML 模板标题
    config.plugin('html').tap(args => {
      args[0].title = 'BirdBlog 管理后台'
      return args
    })
  }
})
