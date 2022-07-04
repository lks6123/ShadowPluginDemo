package com.leelu.shadow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.leelu.constants.Constant
import com.leelu.shadow.databinding.ActivityMainBinding
import com.leelu.shadow.manager.Shadow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.initView()
    }

    private fun ActivityMainBinding.initView() {

        //安装插件1
        btnInstallPlugin.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Shadow.instance.installPlugin("plugin-app-debug.zip")
            }
        }
        //安装插件2
        btnInstallPlugin2.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Shadow.instance.installPlugin("plugin-ablum-debug.zip")
            }
        }

        //加载插件1
        btnLoadPlugin1.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Shadow.instance.loadPlugin(Constant.PART_KEY)
            }
        }

        //加载插件2
        btnLoadPlugin2.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Shadow.instance.loadPlugin(Constant.PART_KEY2)
            }
        }

        //启动插件1
        btnStartPlugin1.setOnClickListener {
            Shadow.instance.startActivity(
                Constant.PART_KEY,
                "com.leelu.plugin_app.MainActivity", null, {
                    //如果通过 handlerPost 消息。 或者延迟一会儿 finish（）. 就可以正常跳转
                    /*Handler(Looper.getMainLooper()).post {
                        finish()
                    }*/

                    //如果直接finish()  在插件中无法跳转其它插件页面。
                    finish()
                }
            )
        }

        //启动插件2
        btnStartPlugin2.setOnClickListener {
            Shadow.instance.startActivity(
                Constant.PART_KEY2,
                "com.leelu.plugin_ablum.MainActivity", null, {

                }
            )
        }
    }

}