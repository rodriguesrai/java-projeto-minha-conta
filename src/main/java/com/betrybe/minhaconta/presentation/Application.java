package com.betrybe.minhaconta.presentation;

import com.betrybe.minhaconta.business.EnergyAccount;
import com.betrybe.minhaconta.business.EnergyBill;
import com.ions.lightdealer.sdk.model.Address;
import com.ions.lightdealer.sdk.model.Client;
import com.ions.lightdealer.sdk.model.ElectronicDevice;
import com.ions.lightdealer.sdk.service.LightDealerApi;
import java.util.Scanner;

/**
 * The type Application.
 */
public class Application {
  Scanner scanner;
  ConsoleUserInterface ui;
  LightDealerApi api;

  /**
   * Constructor that instantiates a new Application.
   */
  public Application(ConsoleUserInterface ui) {
    this.ui = ui;
    this.api = new LightDealerApi();
  }

  /**
   * Req. 4 – Creates CLI menu.
   */
  public void run() {
    String[] menuOptions = {
      "1 - Cadastrar cliente",
      "2 - Cadastrar endereço do cliente",
      "3 - Cadastrar dispositivos do endereço",
      "4 - Estimar conta de energia do endereço",
      "5 - Otimizar conta de energia do endereço",
      "6 - Sair"
    };
    char uiOption;
    do {
      uiOption = ui.inputMenuOption(menuOptions);
      runOptionAction(uiOption);
    } while (uiOption != '6');
  }

  /**
   * Req. 5 – Run menu options.
   */
  public void runOptionAction(char option) {
    switch (option) {
      case '1':
        registerClient();
        break;
      case '2':
        registerClientAddress();
        break;
      case '3':
        registerAddressDevices();
        break;
      case '4':
        estimateAddressBill();
        break;
      case '5':
        optimizeEnergyBill();
        break;
      case '6':
        ui.showMessage("Volte sempre!");
        break;
      default:
        ui.showMessage("Opção inválida!");
    }
  }

  /**
   * Req. 6 – Register client.
   */
  public void registerClient() {
    Client client = new Client();
    ui.fillClientData(client);
    api.addClient(client);
  }

  /**
   * Req. 7 – Register client address.
   */
  public void registerClientAddress() {
    Client client = api.findClient(ui.inputClientCpf());
    if (client == null) {
      ui.showMessage("Pessoa cliente não encontrada!");
      return;
    }

    Address address = new Address();
    ui.fillAddressData(address);
    api.addAddressToClient(address, client);
  }

  /**
   * Req. 8 – Register address devices.
   */
  public void registerAddressDevices() {
    String matricula = ui.inputAddressRegistration();
    Address address = api.findAddress(matricula);
    if (address == null) {
      ui.showMessage("Endereço não encontrado!");
      return;
    }
    Integer devicesAmount = ui.inputNumberOfDevices();
    for (int i = 0; i < devicesAmount; i++) {
      ElectronicDevice device = new ElectronicDevice();
      ui.fillDeviceData(device);
      api.addDeviceToAddress(device, address);
    }
  }

  /**
   * Req. 9 – Estimates the address energy bill.
   */
  public void estimateAddressBill() {
    String matricula = ui.inputAddressRegistration();
    Address address = api.findAddress(matricula);
    if (address == null) {
      ui.showMessage("Endereço não encontrado!");
      return;
    }
    EnergyBill energyBill = new EnergyBill(address, true);
    ui.showMessage("Valor estimado para a conta: " + energyBill.estimate());

  }

  /**
   * Req. 10 – Optimizes the energy bill.
   */
  public void optimizeEnergyBill() {
    Client client = api.findClient(ui.inputClientCpf());
    if (client == null) {
      ui.showMessage("Pessoa cliente não encontrada!");
      return;
    }
    EnergyAccount energyAccount = new EnergyAccount(client);
    suggestReducedUsage(energyAccount);
  }

  /**
   * Req 10 - Aux. Method to display high consumptions devices.
   */
  public void suggestReducedUsage(EnergyAccount energyAccount) {
    ElectronicDevice[] highConsumptionDevices = energyAccount.findHighConsumptionDevices();

    ui.showMessage("Considere reduzir o uso dos seguintes dispositivos:");
    for (ElectronicDevice device : highConsumptionDevices) {
      ui.showMessage(device.getName());
    }
  }
}
