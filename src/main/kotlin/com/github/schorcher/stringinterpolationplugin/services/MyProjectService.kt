package com.github.schorcher.stringinterpolationplugin.services

import com.github.schorcher.stringinterpolationplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
