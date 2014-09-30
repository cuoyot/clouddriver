/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




package com.netflix.spinnaker.kato.deploy.aws.validators
import com.netflix.spinnaker.kato.config.KatoAWSConfig
import com.netflix.spinnaker.kato.deploy.aws.description.TerminateInstancesDescription
import org.springframework.validation.Errors
import spock.lang.Shared
import spock.lang.Specification

class TerminateInstancesDescriptionValidatorSpec extends Specification {

  @Shared
  TerminateInstancesDescriptionValidator validator

  void setupSpec() {
    validator = new TerminateInstancesDescriptionValidator()
    validator.awsConfigurationProperties = new KatoAWSConfig.AwsConfigurationProperties(regions: ["us-west-1"])
  }

  void "invalid instanceIds fail validation"() {
    setup:
    def description = new TerminateInstancesDescription(instanceIds: [""])
    def errors = Mock(Errors)

    when:
    validator.validate([], description, errors)

    then:
    1 * errors.rejectValue("instanceIds", "TerminateInstancesDescription.instanceId.invalid")
  }

  void "unconfigured region fails validation"() {
    setup:
    def description = new TerminateInstancesDescription()
    description.region = "us-west-5"
    def errors = Mock(Errors)

    when:
    validator.validate([], description, errors)

    then:
    1 * errors.rejectValue("region", "TerminateInstancesDescription.region.not.configured")

    when:
    description.region = validator.awsConfigurationProperties.regions.first()
    validator.validate([], description, errors)

    then:
    0 * errors.rejectValue("region", "TerminateInstancesDescription.region.not.configured")
  }
}