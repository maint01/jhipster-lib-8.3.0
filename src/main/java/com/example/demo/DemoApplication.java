package com.example.demo;

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.ContainerMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
public class DemoApplication {


	private static final String CPU = "cpu";
	private static final String MEMORY = "memory";

	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) throws IOException {
		SpringApplication.run(DemoApplication.class, args);

        System.out.println("123");


		// kết nối tới cụm bằng kubernetes.client
		// Đường dẫn đến tệp cấu hình kubeconfig của k8s
//		String filePath = "src/main/resources/local-maik8s.yaml"; // Đường dẫn tới tệp file.txt trong thư mục dự án Spring Boot

//		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//			String line;
//			while ((line = br.readLine()) != null) {
//				System.out.println(line); // In ra từng dòng trong tệp
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

//		// Sử dụng FileReader để đọc cấu hình từ tệp kubeconfig
//		ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(filePath))).build();
//
//		// Đặt cấu hình client làm cơ sở cho thư viện Kubernetes Client
//		Configuration.setDefaultApiClient(client);
//
//		// Bây giờ bạn có thể sử dụng thư viện Kubernetes Client để tương tác với cụm Kubernetes
//		// Ví dụ: Lấy danh sách tất cả các Namespace
//		CoreV1Api api = new CoreV1Api();
//		V1NamespaceList namespaceList = api.listNamespace(null,null, null, null, null, null, null, null, null, null);
//
//		// In ra danh sách các Namespace
//		System.out.println("Namespaces:");
//		for (V1Namespace namespace : namespaceList.getItems()) {
//			System.out.println(namespace.getMetadata().getName());
//		}

		// kêt nối tới cụm bằng fabric8 (giống lib kiên)
		try {
			// Tạo cấu hình Kubernetes = local-maik8s.yaml
			Config kubeConfig = new ConfigBuilder()
					.withApiVersion("v1") // Đặt API version là v1
					.withMasterUrl("https://103.226.248.168:9443/k8s/clusters/c-m-m5l4gnqt") // URL của máy chủ Kubernetes
					.withTrustCerts(true) // Cho phép tin tưởng chứng chỉ SSL
//					.withClientKeyData("YOUR_CLIENT_KEY_DATA") // Dữ liệu client key
//					.withCaCertData("YOUR_CA_CERTIFICATE_DATA") // Dữ liệu certificate authority
					.withOauthToken("kubeconfig-user-7g2tx9q7x5:24jzvsk22gzq7djqdlvcxfxmt885sfc7zpcsgqs4w7dsg6j8jzlwnm") // Token của user
					.build();

			// Sử dụng KubernetesClientBuilder để tạo kết nối tới Kubernetes với cấu hình từ kubeconfig
			KubernetesClient client = new KubernetesClientBuilder()
					.withConfig(kubeConfig)
					.build();

			// Bây giờ bạn có thể sử dụng client để tương tác với cụm Kubernetes
			// Ví dụ: Lấy danh sách tất cả các Namespace
			client.namespaces().list().getItems().forEach(namespace -> {
				System.out.println("Namespace: " + namespace.getMetadata().getName());
			});

//			1. create
//			// create deploy (be,fe,db,..)
//			// Đường dẫn đến tệp YAML chứa định nghĩa Deployment (ví dụ: file.yaml)
////			String yamlFilePath = "/path/to/deployment.yaml";
//			// Đường dẫn đến tệp YAML chứa định nghĩa Deployment (ví dụ: file.yaml)
//			String yamlFilePath = "src/main/resources/deployment.yaml";
//
//			// Đọc nội dung tệp YAML bằng Files
//			String yamlContent = new String(Files.readAllBytes(Paths.get(yamlFilePath)), StandardCharsets.UTF_8);
//
//			// Sử dụng thư viện YAML để phân tích chuỗi YAML và chuyển thành đối tượng Deployment
//			Deployment deployment = Serialization.unmarshal(yamlContent, Deployment.class);
//
//			// Bây giờ bạn đã có đối tượng Deployment sẵn sàng để sử dụng
//			System.out.println("Deployment Name: " + deployment.getMetadata().getName());
//			System.out.println("Replicas: " + deployment.getSpec().getReplicas());
////			String namespace = deployment.getMetadata().getNamespace();
//			// In ra namespace
////			System.out.println("Namespace of Deployment " + deployment.getMetadata().getName() + ": " + namespace);
//			// ... và các thông tin khác
//
//			deployment = client.apps().deployments().inNamespace("default").resource(deployment).create();
//			System.out.println("Created deployment:" + deployment);


//			2. stop/start/update
//			// stop scale = 0
//			// start scale = 1
//			// Tên của Deployment cần scale
//			// update ram, cpu -> sửa file yaml xong; deploy lại đè lên (như create)
//			String deploymentName = "my-deployment-tuan"; // Thay thế bằng tên Deployment của bạn
//
//			// Số lượng replicas bạn muốn scale (ví dụ: 0; 1)
//			int desiredReplicas = 1;
//
//			// Lấy thông tin Deployment từ Kubernetes
//			Deployment currentDeployment = client.apps().deployments().inNamespace("default").withName(deploymentName).get();
//
//			if (currentDeployment != null) {
//				client.apps().deployments().inNamespace("default").withName("my-deployment-tuan").scale(0, true);
//				System.out.println("Deployment " + deploymentName + " scaled to " + desiredReplicas + " replicas.");
//			} else {
//				System.err.println("Deployment not found.");
//			}

//			3. metric
			//metric monitoring can duoc cai dat metrics-server hoặc lấy thông qua cách khác như rancher
			System.out.println("==== Node Metrics  ====");
			if (!client.supports(NodeMetrics.class)) {
				logger.warn("Metrics API is not enabled in your cluster");
				// bật lên bằng cách chạy thêm metrics-server trong cụm k8s; kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
				return;
			}
			logger.info("==== Node Metrics  ====");
			client.top().nodes().metrics().getItems().forEach(nodeMetrics -> logger.info("{}\tCPU: {}{}\tMemory: {}{}",
					nodeMetrics.getMetadata().getName(),
					nodeMetrics.getUsage().get(CPU).getAmount(), nodeMetrics.getUsage().get(CPU).getFormat(),
					nodeMetrics.getUsage().get(MEMORY).getAmount(), nodeMetrics.getUsage().get(MEMORY).getFormat()));

			final String namespace = Optional.ofNullable(client.getNamespace()).orElse("default");
			logger.info("==== Pod Metrics ====");
			Map<String, Node> mapNode = client.nodes().list().getItems().stream().collect(Collectors.toMap(node -> node.getMetadata().getName(), node -> node));
			client.top().nodes().metrics().getItems()
							.forEach(metric -> {
								Node node = mapNode.get(metric.getMetadata().getName());
								logger.info("{}\t\tCPU: {}/{}\tMemory: {}/{}",
										node.getMetadata().getName(),
										metric.getUsage().get(CPU).getAmount(), node.getStatus().getAllocatable().get(CPU).getAmount(),
										metric.getUsage().get(MEMORY).getAmount(), node.getStatus().getAllocatable().get(MEMORY).getFormat());
							});

			client.pods().inNamespace(namespace).list().getItems().stream().findFirst().map(pod -> {
				logger.info("==== Individual Pod Metrics ({}) ====", pod.getMetadata().getName());
				try {
					return client.top().pods().metrics(namespace, pod.getMetadata().getName());
				} catch (KubernetesClientException ex) {
					if (ex.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
						logger.info(" - Pod has not reported any metrics yet");
					} else {
						logger.warn(" - Error retrieving Pod metrics: {}", ex.getMessage());
					}
					return null;
				}
			}).ifPresent(podMetrics -> podMetrics.getContainers()
					.forEach(containerMetrics -> logger.info("{}\t{}\tCPU: {}{}\tMemory: {}{}",
							podMetrics.getMetadata().getName(), containerMetrics.getName(),
							containerMetrics.getUsage().get(CPU).getAmount(), containerMetrics.getUsage().get(CPU).getFormat(),
							containerMetrics.getUsage().get(MEMORY).getAmount(), containerMetrics.getUsage().get(MEMORY).getFormat())));


			// Đừng quên đóng kết nối sau khi sử dụng xong
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
