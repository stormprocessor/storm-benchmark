(ns storm.benchmark.messaging-benchmark-test
  (:use [clojure test])
  (:import [storm.benchmark MessagingTest]))

(deftest test-messaging 
	 (MessagingTest/benchmark_netty)
	 (MessagingTest/benchmark_zmq)
)		