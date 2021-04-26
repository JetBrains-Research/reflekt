job("Build") {
    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew build  
          """
        }
    }
}

job("Test") {
    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew test  
          """
        }
    }
}

job("Release") {
    startOn {
        gitPush {
            enabled = false
        }
    }

    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew publish    
          """
        }
    }
}
