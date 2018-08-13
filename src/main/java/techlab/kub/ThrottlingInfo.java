/*
 * Copyright 2016 Red Hat, Inc.
 * <p>
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package techlab.kub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * A sample transform
 */
@Component(value = "throttlingInfoBean")
public class ThrottlingInfo {


	@Autowired
	private KubernetesClient kubernetesClient;

	@Value("${camel.springboot.name}")
	private String appName;

	@Value("${throttlingCallsPerSec}")
	private Integer throttlingCallsPerSec;
	
	@Value("${throttlingCallsPerSec}") //give it an initial value
	private Integer localThrottlingCallsPerSec;
	
	
	public KubernetesClient getKubernetesClient() {
		return kubernetesClient;
	}

	public void setKubernetesClient(KubernetesClient kubernetesClient) {
		this.kubernetesClient = kubernetesClient;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Integer getLocalThrottlingCallsPerSec() {
		return localThrottlingCallsPerSec;
	}

	public void setLocalThrottlingCallsPerSec(Integer localThrottlingCallsPerSec) {
		this.localThrottlingCallsPerSec = localThrottlingCallsPerSec;
	}

	public Integer refreshThrottlingInfo() {
		
		Integer nbORunningPods = getNbOfRunningPods();
		
		if (nbORunningPods==0) {
			localThrottlingCallsPerSec = throttlingCallsPerSec;
		}else
		{
			localThrottlingCallsPerSec = Math.floorDiv(throttlingCallsPerSec, nbORunningPods);
		}
		
		return localThrottlingCallsPerSec;
		
	}

	public Integer getNbOfRunningPods() {
		List<Pod> pods = kubernetesClient.pods().withLabel("app",appName).list().getItems();
		Integer runningPods=0;

		for (Pod pod : pods) {
			if (pod != null) {
				//Loop through all containers to see if all are ready				
				Integer nbContainers = pod.getStatus().getContainerStatuses().size();
				Integer nbReadyContainers = 0;
				System.out.println("Pod :" +  pod.getMetadata().getName());
				for (ContainerStatus containerStatus: pod.getStatus().getContainerStatuses()) {
					if (containerStatus!=null ) {
						System.out.println(containerStatus.getName() + " " + containerStatus.getReady().toString());
						if (containerStatus.getReady()) {
							nbReadyContainers++;
						}

					}
				}
				//If all containers are ready than increment running pods.
				if (nbContainers.compareTo(nbReadyContainers) == 0) {
					runningPods++;
				}
			}
		
		}
		return runningPods;
	}
}

