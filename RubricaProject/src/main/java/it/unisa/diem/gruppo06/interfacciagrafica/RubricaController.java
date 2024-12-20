package it.unisa.diem.gruppo06.interfacciagrafica;

import it.unisa.diem.gruppo06.gestionecontatti.Contatto;
import it.unisa.diem.gruppo06.gestionecontatti.InterfacciaRubrica;
import it.unisa.diem.gruppo06.gestionecontatti.Rubrica;
import it.unisa.diem.gruppo06.gestionefile.FileManager;
import it.unisa.diem.gruppo06.main.Main;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * @brief Classe che gestisce l'interfaccia principale visualizzata dall'utente
 * 
 * La classe 'RubricaController' gestisce le azioni associate ad i tasti
 * che può premere l'utente nella scena principale, che mostra la lista 
 * di contatti presenti in rubrica e consente di effettuare le operazioni previste 
 * su quest'ultima
 * 
 * 
 * 
 *
 */
public class RubricaController implements Initializable{

    
    @FXML
    private Button infoBtn;
    @FXML
    private Button eliminaBtn;
    @FXML
    private TableView<Contatto> contactsTable;
    @FXML
    private TableColumn<Contatto, String> cognomi;
    @FXML
    private TableColumn<Contatto, String> nomi;
    @FXML
    private TableColumn<Contatto, String> telefoni;
    @FXML
    private TableColumn<Contatto, String> email;
    @FXML
    private TextField searchField;
    @FXML
    private MenuItem salvaRubrica;
    @FXML
    private MenuItem importaRubrica;
    @FXML
    private Button aggiungiBtn;
    @FXML
    private Button pennaBtn;
    @FXML
    private Button exitBtn;
    
    private FileManager fileManager;
    
    private ObservableList<Contatto> contatti;
    
    private static InterfacciaRubrica rubrica = new Rubrica();

    

  

    /**
    * @brief Inizializza il controller
    * 
    * Il metodo inizializza tutti gli attributi (non statici) del controller
    * istanziando una ObservableArrayList ed un FileManager, inoltre
    * costruisce la TableView che conterrà le informazioni di ogni contatto
    * 
    * @param[in] url Percorso del file FXML associato al controller
    * @param[in] rb Collezione di risorse localizzate per l'applicazione
    * 
    * @post
    * La TableView è stata costruita correttamente
    * @post
    * Gli attributi non statici sono stati correttamente inizializzati
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fileManager= new FileManager();
        contatti = FXCollections.observableArrayList(rubrica.getLista());
        
        infoBtn.setDisable(true);
        eliminaBtn.setDisable(true);
        pennaBtn.setDisable(true);

        cognomi.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        nomi.setCellValueFactory(new PropertyValueFactory<>("nome"));
        telefoni.setCellValueFactory(new PropertyValueFactory<>("numeroTelefono1"));
        email.setCellValueFactory(new PropertyValueFactory<>("email1"));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            aggiornaTabella(newValue);
        });
        
        contactsTable.setItems(contatti);
        
        contactsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean contattoSelezionato = (newValue != null);
            infoBtn.setDisable(!contattoSelezionato);
            eliminaBtn.setDisable(!contattoSelezionato);
            pennaBtn.setDisable(!contattoSelezionato);
        });
        
        contactsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {          
            boolean contattoSelezionato = (newValue != null);
            infoBtn.setDisable(!contattoSelezionato);
            eliminaBtn.setDisable(!contattoSelezionato);
            pennaBtn.setDisable(!contattoSelezionato);
        });
        
        assert(contactsTable.getColumns().size()==3);
        assert(fileManager!=null) && (contatti!=null);
    }

    
    /**
    * @brief Restituisce l'attributo statico 'rubrica'
    * 
    * @return Oggetto di tipo InterfacciaRubrica contenente la lista dei contatti
    * 
    * 
    */
    public static InterfacciaRubrica getRubrica(){
        return rubrica;
    }
    

    @FXML
    private void visualizzaInfoBtn(ActionEvent event) throws IOException {
        Contatto c = contactsTable.getSelectionModel().getSelectedItem();//prelevo il contatto selezionato
            FXMLLoader loader = Main.setRootAndGetLoader("SecondaView");

            InfoController info = loader.getController();
            info.setField(c,false);//imposto i field solo per la visualizzazione
    }

    @FXML
    private void eliminaContattoBtn(ActionEvent event) {
        Contatto c = contactsTable.getSelectionModel().getSelectedItem();//preleviamo l'elemento selezionato
        rubrica.eliminaContatto(c);
        contatti.setAll(rubrica.getLista());
        contactsTable.setItems(contatti);
        searchField.setText("");
    }

    @FXML
    private void modificaContattoBtn(ActionEvent event) throws IOException {
        Contatto c = contactsTable.getSelectionModel().getSelectedItem();
        FXMLLoader loader = Main.setRootAndGetLoader("SecondaView");
        InfoController info = loader.getController();
        info.setField(c,true);
    }
    
     @FXML
    private void exit(ActionEvent event) {
        Platform.exit();
    }

    private void aggiornaTabella(String searchText){
        ObservableList<Contatto> contattiFiltrati;
        contattiFiltrati = FXCollections.observableArrayList(rubrica.cercaContatto(searchText));
        contactsTable.setItems(contattiFiltrati);
    }
    
   

    @FXML
    private void salvaSuFileBtn(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegliere il file in cui esportare i contatti");
     
        Window window = (contactsTable.getParent().getScene().getWindow());
        File selectedFile = fileChooser.showSaveDialog(window);
        
        fileManager.salvaSuFile(selectedFile);
    }

    @FXML
    private void caricaDaFileBtn(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegliere il file da cui importare i contatti");

        Window window = contactsTable.getScene().getWindow();
        
        File selectedFile = fileChooser.showOpenDialog(window);
        if(selectedFile!=null){
            rubrica= fileManager.caricaDaFile(selectedFile);
            contatti.setAll(rubrica.getLista());
        }
    }

    @FXML
    private void aggiungiContattoBtn(ActionEvent event) throws IOException {
        contatti.setAll(rubrica.getLista());
        Main.setRoot("SecondaView");
    }
    
    private void createAndShowAlert(AlertType type, String title, String headerText, String contentText){
        Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            alert.showAndWait();    
    }
}