@Library('Pipelines') _
pipelines = new com.datwyler.distrelec.jenkins.pipeline.Pipelines()

properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '10']]]);
pipelines.hybrisBuildPipeline()
