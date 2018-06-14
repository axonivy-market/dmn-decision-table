package com.axonivy.ivy.process.element.blockchain.exec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

import com.axonivy.ivy.process.element.blockchain.EthereumProperties;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.project.IvyProjectNavigationUtil;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.log.Logger;
import ch.ivyteam.util.IvyRuntimeException;

public final class EthereumExecutor
{
  private static final BigInteger DEFAULT_GAS_PRIZE = BigInteger.valueOf(40000000000l);
  private static final BigInteger DEFAULT_GAS_LIMIT = BigInteger.valueOf(1500000l);

  private static final Logger LOGGER = Logger.getLogger(EthereumExecutor.class);

  Contract ethContract;
  Method contractMethod;

  public EthereumExecutor(String contract, String function, Map<String, String> properties)
  {
    initialize(contract, function, properties);
  }

  private void initialize(String contract, String function, Map<String, String> properties)
  {
    String credentialsFile = properties.get(EthereumProperties.CREDENTIALS);
    String password = properties.get(EthereumProperties.PASSWORD);
    String url = properties.get(EthereumProperties.NETWORK_URL);
    String contractAddress = properties.get(EthereumProperties.CONTRACT_ADDRESS);

    ethContract = loadOrDeployContract(contract, url, contractAddress, credentialsFile, password);
    contractMethod = loadMethod(function, ethContract);
  }

  public RemoteCall<?> execute(Object[] callParams)
  {
    try
    {
      return (RemoteCall<?>) contractMethod.invoke(ethContract, callParams);
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
    {
      throw new IvyRuntimeException("Could not execute method " + contractMethod.getName(), ex);
    }
  }

  private Method loadMethod(String function, Contract contract)
  {
    Method[] methods = contract.getClass().getDeclaredMethods();
    Method chosenMethod = null;
    for (Method method : methods)
    {
      if (method.toString().equals(function))
      {
        chosenMethod = method;
        break;
      }
    }
    return chosenMethod;
  }

  private Web3j buildWeb3j(String url)
  {
    HttpService httpService = new HttpService(url);
    return Web3j.build(httpService);
  }

  private Credentials loadCredentials(String passwd, String source) throws Exception
  {
    return WalletUtils.loadCredentials(passwd, source);
  }

  @SuppressWarnings("unchecked")
  private Contract loadOrDeployContract(String clazz, String url, String contractAddress, String file, String passwd)
  {
    Web3j web3 = buildWeb3j(url);
    Method accessMethod;
    try
    {
      Credentials credentials = loadCredentials(passwd, file);
      Class<? extends Contract> contractClass = (Class<? extends Contract>) loadClassWithCurrentClassloader(clazz);
      if (StringUtils.isEmpty(contractAddress))
      {
        accessMethod = contractClass.getMethod("deploy", Web3j.class, Credentials.class, BigInteger.class, BigInteger.class);
        Contract contract = (Contract) ((RemoteCall<?>) accessMethod.invoke(null, web3, credentials, DEFAULT_GAS_PRIZE, DEFAULT_GAS_LIMIT)).send();
        if(!"0x1".equals(contract.getTransactionReceipt().get().getStatus()))
        {
          LOGGER.error("Could not deploy contract of class " + clazz + "; TxReceipt=" + contract.getTransactionReceipt().get());
        }
        LOGGER.info("Deployed new contract " + contractClass.getName() + " to address " + contract.getContractAddress());
        return contract;
      }
      accessMethod = contractClass.getMethod("load", String.class, Web3j.class, Credentials.class, BigInteger.class, BigInteger.class);
      return (Contract) accessMethod.invoke(null, contractAddress, web3, credentials, DEFAULT_GAS_PRIZE, DEFAULT_GAS_LIMIT);
    }
    catch (Exception ex)
    {
      throw new IvyRuntimeException("Could not get contract of class " + clazz, ex);
    }
  }

  private static Class<?> loadClassWithCurrentClassloader(String fullyQualifiedClassName) throws ClassNotFoundException
  {
    try
    {
      return EthereumExecutor.class.getClassLoader().loadClass(fullyQualifiedClassName);
    }
    catch (ClassNotFoundException ex)
    {
      return loadClassWithProjectClassloader(fullyQualifiedClassName);
    }
  }

  private static Class<?> loadClassWithProjectClassloader(String fullyQualifiedClassName) throws ClassNotFoundException
  {
    IProcessModelVersion pmv = DiCore.getGlobalInjector().getInstance(IProcessModelVersion.class);
    ClassLoader projectClassLoader = Sudo.exec(() -> IvyProjectNavigationUtil.getIvyProject(pmv.getProject()).getProjectClassLoader());
    return ClassUtils.getClass(projectClassLoader, fullyQualifiedClassName, false);
  }

  public Parameter[] getMethodParams()
  {
    return contractMethod.getParameters();
  }
}
