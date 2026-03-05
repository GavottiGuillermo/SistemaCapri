package org.sistema;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.google.cloud.storage.Blob;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.sistema.ModuloCargaArchivo.*;
import org.sistema.ModuloCashFlow.RegistroTransaccionCF;
import org.sistema.ModuloVentas.RegistroArticuloVentas;
import org.sistema.config.DatabaseConfig;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public class Controlador_Pestanias implements Initializable {
    ////////////VARIABLES CASH FLOW ////////////////////////
    @FXML private TableView<RegistroTransaccionCF> tblCashFlow;
    @FXML private TableColumn<RegistroTransaccionCF, String> colFechaTransaccion;
    @FXML private TableColumn<RegistroTransaccionCF, String> colObservacion;
    @FXML private TableColumn<RegistroTransaccionCF, Double> colDebe;
    @FXML private TableColumn<RegistroTransaccionCF, Double> colHaber;
    @FXML private TableColumn<RegistroTransaccionCF, Double> colSaldo;
    @FXML private Array analisisArray;
    @FXML private DatePicker dtFechaInicialCF, dtFechaFinalCF;
    @FXML private Button btnConsultarCF;
    @FXML private Button btnLimpiarCF;
    @FXML private Label lbCantVentas;
    @FXML private Label lbTotalIngresos;
    @FXML private Label lbTotalGastos;
    @FXML private Label lbSaldoTotal;
    @FXML private Label lbMayorIngreso;
    @FXML private Label lbCantGasto;
    @FXML private Label lbDiaMayorIngreso;
    @FXML private Label lbDiaMayorGasto;
    private final ObservableList<RegistroTransaccionCF> lstObsCashflow = FXCollections.observableArrayList();

    ////////////VARIABLES VENTAS ////////////////////////
    @FXML private TableView<RegistroArticuloVentas> tblArticulosStock;
    @FXML private TableColumn<RegistroArticuloVentas, String> colIdArticuloVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colEstadoVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colNombreArticuloVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colCategoriaVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colColorVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colTalleVentas;
    @FXML private TableColumn<RegistroArticuloVentas, Double> colPrecioEfectivoVentas;
    @FXML private TableColumn<RegistroArticuloVentas, Double> colPrecioTransferenciaVentas;
    @FXML private TableColumn<RegistroArticuloVentas, Void> colAccionesVentas;
    @FXML private TableView<RegistroArticuloVentas> tblArticuloEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colIdArticuloEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colNomArticuloEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colCategoriaEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colColorEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colTalleEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colPrecioEfectivoEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colPrecioTransferenciaEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, Void> colAccionesEstaCompra;
    @FXML private TextField fldBuscarNumArticulo;
    @FXML private TextField fldBuscarNomPrenda;
    @FXML private TextField fldNombreCliente;
    @FXML private TextField fldBuscarEstado;
    @FXML private TextField fldBuscarCategoria;
    String nombreCliente;
    String instagramCliente;
    String correoCliente;
    @FXML private TextField fldInstagramCliente;
    @FXML private TextField fldCorreoCliente;
    @FXML private Button btnRefrescarStock;
    @FXML private Label lbCantArticulos;
    @FXML private Label lbPrecioTotal;
    @FXML private Button btnFinalizarCompra;
    @FXML private Button btnCancelarCompra;
    @FXML private ComboBox cbMetodoPago;
    private final ObservableList<RegistroArticuloVentas> listaStock = FXCollections.observableArrayList();
    private FilteredList<RegistroArticuloVentas> listaFiltrada;
    private final ObservableList<RegistroArticuloVentas> listaCompra = FXCollections.observableArrayList();
    private double totalCompra = 0.0;
    ////////////VARIABLES CARGA ////////////////////////
    @FXML private Button btnBuscarArchivo;
    @FXML private Button btnCargarArchivo;
    @FXML private Label rutaArchivoCargado;
    @FXML private Button btnAgregarProducto;
    @FXML private Button btnFinalizarCarga;
    @FXML private TextField fldCostoLote,fldObservacion,fldInversor,fldNombreProductoLote,fldCategoriaProductoLote,
            fldColorProductoLote,fldTalleProductoLote,fldPrecioCompraProductoLote,fldPrecioVentaProductoLote;
    @FXML private TableView<RegistroProducto> tblProductosEsteLote;
    private ObservableList<RegistroProducto> lstCargaProductos = FXCollections.observableArrayList();
    @FXML private TableColumn<RegistroProducto, String> colNombreProdLote;
    @FXML private TableColumn<RegistroProducto, String> colCategoriaProdLote;
    @FXML private TableColumn<RegistroProducto, String> colColorProdLote;
    @FXML private TableColumn<RegistroProducto, String> colTalleProdLote;
    @FXML private TableColumn<RegistroProducto, Double> colPrecioCompraProdLote;
    @FXML private TableColumn<RegistroProducto, Double> colPrecioVentaProdLote;
    @FXML private File archivoSeleccionado;
    @FXML private List<RegistroLote> lstLotes;
    @FXML private List<RegistroProducto> lstProductos;
    @FXML private List<RegistroPago> lstPagos;
    @FXML private List<RegistroGastoExtra> lstGastosExtra;
    @FXML private List<RegistroCliente> lstClientes;
    @FXML Connection conexionABBDD;
    private boolean modoOffline = false; // Indica si la app está en modo sin conexión a BD

    ////////////PESTAÑA CONSULTAR/MODIFICAR LISTAS ////////////////////////
    @FXML private ComboBox<String> cmbListas;
    @FXML private String listaActual = null;
    private Button btnRefrescarModifLista;
    @FXML private TableView<ObservableList<String>> tblConsulta;
    @FXML private TableColumn<ObservableList<String>, String> colGenerica; // Se crean dinámicamente
    private final String[] opcionesListas = {"productos", "lotes", "pagos", "gastos_extra", "clientes"};

    ////////////PESTAÑA PEDIDOS ////////////////////////
    @FXML private TableView<ObservableList<String>> tblPedidos;
    @FXML private TableColumn<ObservableList<String>, String> colIdPedido;
    @FXML private TableColumn<ObservableList<String>, String> colFechaPedido;
    @FXML private TableColumn<ObservableList<String>, String> colEstadoPedido;
    @FXML private TableColumn<ObservableList<String>, String> colMontoPedido;
    @FXML private TableColumn<ObservableList<String>, String> colNombreClientePedido;
    @FXML private TableColumn<ObservableList<String>, Void> colAccionesPedido;
    @FXML private TextField fldBuscarArticuloPedido, fldBuscarEstadoPedido;
    @FXML private DatePicker dtFechaInicialPedido, dtFechaFinalPedido;
    @FXML private Button btnRefrescarPedidos;
    private final ObservableList<ObservableList<String>> listaPedidos = FXCollections.observableArrayList();
    ////////////PESTAÑA ARTICULOS WEB ////////////////////////
    @FXML private TextField fldIdArticuloWeb, fldPrendaWeb;
    @FXML private ComboBox<String> cmbCategoriaWeb;
    @FXML private Button btnSeleccionarTxtWeb, btnSeleccionarImagenWeb, btnSubirArticuloWeb;
    @FXML private Label lblRutaTxtWeb, lblRutaImagenWeb;
    private File archivoTxtSeleccionado, archivoImagenSeleccionada;
    @FXML
    private TableView<RegistroArticuloVentas> tblProductosWeb;
    @FXML
    private TextField fldNombreTarjeta, fldTextoTarjeta, fldPrecioTarjeta;
    @FXML
    private TextArea fldDetalleTarjeta;
    @FXML private TableColumn<RegistroArticuloVentas, Integer> colIdWeb;
    @FXML private TableColumn<RegistroArticuloVentas, String> colPrendaWeb;
    @FXML private TableColumn<RegistroArticuloVentas, String> colEstadoWeb;
    @FXML private TableColumn<RegistroArticuloVentas, String> colPublicadoWeb;
    @FXML private TableColumn<RegistroArticuloVentas, Void> colAccionesWeb;
    @FXML private TextField fldBuscarArticuloWeb;
    @FXML private Button btnGenerarSubirArticulo;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button btnRefrescarWeb;
    @FXML private Label lblImagenSeleccionada;
    @FXML private ComboBox cmbTalleWeb;
    @FXML private CheckBox chkNovedad;
    @FXML private VBox dropZoneImagen;
    private File imagenSeleccionada;

    private final ObservableList<RegistroArticuloVentas> listaProductosWeb = FXCollections.observableArrayList();
    private FilteredList<RegistroArticuloVentas> productosFiltrados;
    @Override public void initialize(URL location, ResourceBundle resources) {

        try{
            ////////////PESTAÑA ARTICULOS WEB ////////////////////////
            configurarTablaWeb();
            configurarSeleccionTablaWeb();
            configurarFiltroTablaWeb();
            configurarDragAndDropImagen();
            obtenerProductosDesdeBD();
            btnRefrescarWeb.setOnAction(event -> refrescarTablaWeb());
            GoogleCloudConfig.configureGoogleCredentials();
            ////////////PESTAÑA VENTAS ////////////////////////
            btnRefrescarStock.setOnAction(event -> refrescarTablaStock());
            btnFinalizarCompra.setDisable(true);
            btnCancelarCompra.setDisable(true);
            cargarDatosDesdeBBDD();
            configurarTablaStock();
            configurarTablaCompra();
            actualizarResumen();
            inicializarFiltrado();
            btnCancelarCompra.setOnAction(event -> {
                cancelarCompra();
            });
            btnFinalizarCompra.setOnAction(event -> {
                finalizarCompra();
            });
            ////////////PESTAÑA CASHFLOW ////////////////////////
            configurarTablaCashFlow();
            ////////////PESTAÑA CARGA ////////////////////////
            btnCargarArchivo.setDisable(true);
            rutaArchivoCargado.setText("");
            btnBuscarArchivo.setOnAction(event -> buscarArchivo());
            btnCargarArchivo.setOnAction(event -> {
                try {
                    cargarArchivo();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            configurarCargaLotes();
            ////////////PESTAÑA CONSULTAR/MODIFICAR LISTAS ////////////////////////
            cmbListas.getItems().addAll(opcionesListas);
            cmbListas.setValue("productos"); // Predeterminado
            cmbListas.setOnAction(event -> {
                try {
                    cargarLista(cmbListas.getValue());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                cargarLista("productos"); // Carga inicial
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            ////////////PESTAÑA PEDIDOS ////////////////////////
            configurarTablaPedidos();
            btnRefrescarPedidos.setOnAction(event -> refrescarTablaPedidos());
            refrescarTablaPedidos();
        }catch (Exception e) {
            System.err.println("❌ Error al inicializar el controlador.");
            e.printStackTrace();
            mostrarAlerta("Error de Inicialización", "Ocurrió un problema al cargar los datos iniciales.", Alert.AlertType.ERROR);
        }

    }

    ////////////PESTAÑA ARTICULOS WEB ////////////////////////
    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png")
        );

        File file = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());
        if (file != null) {
            imagenSeleccionada = file;
            lblImagenSeleccionada.setText(file.getName());
        }
    }

    private void obtenerProductosDesdeBD() {
        listaProductosWeb.clear();

        try {
            conectarABBDDConReintentos();
            String query = "SELECT id_articulo, prenda, estado, categoria, color, talle, " +
                    "precio_venta_efectivo, precio_venta_transferencia, publicado_en_web " +
                    "FROM productos WHERE estado = 'Disponible' ORDER BY id_articulo ASC";

            Statement stmt = conexionABBDD.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                RegistroArticuloVentas producto = new RegistroArticuloVentas(
                        rs.getInt("id_articulo"),
                        rs.getString("prenda"),
                        rs.getString("estado"),
                        rs.getString("categoria"),
                        rs.getString("estado"),
                        rs.getString("talle"),
                        rs.getDouble("precio_venta_efectivo"),
                        rs.getDouble("precio_venta_transferencia"),
                        rs.getString("publicado_en_web")
                );
                listaProductosWeb.add(producto);
            }

            productosFiltrados = new FilteredList<>(listaProductosWeb, p -> true);
            tblProductosWeb.setItems(productosFiltrados);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos desde la base de datos", Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            desconexionABBDD();
        }
    }
    private void configurarTablaWeb() {
        colIdWeb.setCellValueFactory(new PropertyValueFactory<>("idArticulo"));
        colPrendaWeb.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));
        colEstadoWeb.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colPublicadoWeb.setCellValueFactory(new PropertyValueFactory<>("publicadoEnWeb"));

        // Configurar columna de acciones
        colAccionesWeb.setCellFactory(column -> new TableCell<RegistroArticuloVentas, Void>() {
            private final Button btnQuitar = new Button("Quitar de la Web");

            {
                btnQuitar.setOnAction(event -> {
                    RegistroArticuloVentas producto = getTableView().getItems().get(getIndex());
                    if (producto != null) {
                        quitarDeLaWeb(producto); // Call the method
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RegistroArticuloVentas producto = getTableView().getItems().get(getIndex());
                    if (producto != null && "true".equalsIgnoreCase(producto.getPublicadoEnWeb())) {
                        setGraphic(btnQuitar); // Show the button if the product is published
                    } else {
                        setGraphic(null); // Hide the button otherwise
                    }
                }
            }
        });
        colPublicadoWeb.setCellFactory(column -> new TableCell<RegistroArticuloVentas, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equalsIgnoreCase("true")) {
                        setStyle("-fx-background-color: lightgreen; -fx-text-fill: black;");
                    } else if (item.equalsIgnoreCase("false")) {
                        setStyle("-fx-background-color: lightcoral; -fx-text-fill: black;");
                    } else {
                        setStyle(""); // Default style
                    }
                }
            }
        });

        if (!tblProductosWeb.getColumns().contains(colAccionesWeb)) {
            tblProductosWeb.getColumns().add(colAccionesWeb);
        }
    }
    private void configurarFiltroTablaWeb() {
        productosFiltrados = new FilteredList<>(listaProductosWeb, p -> true);
        tblProductosWeb.setItems(productosFiltrados);

        fldBuscarArticuloWeb.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarProductosWeb(newValue);
        });
    }

    private void filtrarProductosWeb(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            productosFiltrados.setPredicate(p -> true);
        } else {
            productosFiltrados.setPredicate(producto ->
                    producto.getNombreArticulo().toLowerCase().contains(filtro.toLowerCase()) ||
                            String.valueOf(producto.getIdArticulo()).contains(filtro)
            );
        }
    }

    private void configurarDragAndDropImagen() {
        if (dropZoneImagen == null) {
            System.err.println("dropZoneImagen no está inicializado");
            return;
        }

        // Configurar eventos de drag over (cuando el archivo está sobre la zona)
        dropZoneImagen.setOnDragOver(event -> {
            if (event.getGestureSource() != dropZoneImagen && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
                // Cambiar estilo para indicar que se puede soltar
                dropZoneImagen.setStyle("-fx-border-color: #4CAF50; -fx-border-style: dashed; -fx-border-width: 2; -fx-background-color: #e8f5e9;");
            }
            event.consume();
        });

        // Configurar evento cuando el drag sale de la zona
        dropZoneImagen.setOnDragExited(event -> {
            // Restaurar estilo original
            dropZoneImagen.setStyle("-fx-border-color: #cccccc; -fx-border-style: dashed; -fx-border-width: 2; -fx-background-color: #f9f9f9;");
            event.consume();
        });

        // Configurar evento cuando se suelta el archivo
        dropZoneImagen.setOnDragDropped(event -> {
            javafx.scene.input.Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                java.util.List<java.io.File> files = db.getFiles();
                if (!files.isEmpty()) {
                    java.io.File file = files.get(0);

                    // Validar que sea una imagen
                    String fileName = file.getName().toLowerCase();
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                        fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".bmp")) {

                        imagenSeleccionada = file;
                        lblImagenSeleccionada.setText(file.getName());
                        mostrarAlerta("Imagen Seleccionada", "Imagen cargada: " + file.getName(), Alert.AlertType.INFORMATION);
                        success = true;
                    } else {
                        mostrarAlerta("Error", "Por favor selecciona un archivo de imagen válido (jpg, jpeg, png, gif, bmp)", Alert.AlertType.ERROR);
                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();

            // Restaurar estilo original
            dropZoneImagen.setStyle("-fx-border-color: #cccccc; -fx-border-style: dashed; -fx-border-width: 2; -fx-background-color: #f9f9f9;");
        });
    }

    @FXML
    private void configurarSeleccionTablaWeb() {
        tblProductosWeb.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fldNombreTarjeta.setText(newSelection.getNombreArticulo());
                // Convertir el precio a entero directamente
                int precioEntero = (int) newSelection.getPrecioTransferencia();
                fldPrecioTarjeta.setText(String.valueOf(precioEntero));
            }
        });
    }

    @FXML
    private void generarYSubirArticulo() {
        if (tblProductosWeb.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta("Error", "Debe seleccionar un artículo de la tabla", Alert.AlertType.ERROR);
            return;
        }

        if (imagenSeleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una imagen", Alert.AlertType.ERROR);
            return;
        }

        String carpeta = null;
        try {
            // Obtener valores de los campos, usando "null" para campos vacíos
            String titulo = fldNombreTarjeta.getText();
            titulo = (titulo == null || titulo.trim().isEmpty()) ? "null" : titulo.trim();

            String texto = fldTextoTarjeta.getText();
            texto = (texto == null || texto.trim().isEmpty()) ? "null" : texto.trim();

            String precioStr = fldPrecioTarjeta.getText();
            int precio = 0;
            if (precioStr != null && !precioStr.trim().isEmpty()) {
                try {
                    precio = Integer.parseInt(precioStr.trim());
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "El precio debe ser un número válido", Alert.AlertType.ERROR);
                    return;
                }
            }

            String detalle = fldDetalleTarjeta.getText();
            detalle = (detalle == null || detalle.trim().isEmpty()) ? "null" : detalle.trim();

            String categoria = cmbCategoriaWeb.getValue();
            categoria = (categoria == null || categoria.trim().isEmpty()) ? "null" : categoria.trim();

            String talle = (String) cmbTalleWeb.getValue();
            talle = (talle == null || talle.trim().isEmpty()) ? "null" : talle.trim();

            int idArticulo = tblProductosWeb.getSelectionModel().getSelectedItem().getIdArticulo();

            // Update the price in the database only if price is valid
            if (precio > 0) {
                actualizarPrecioVentaTransferencia(idArticulo, precio);
            }

            // Generate the base name for the files
            String nombreBase = idArticulo + "-" + tblProductosWeb.getSelectionModel().getSelectedItem().getNombreArticulo();
            carpeta = "Novedades/" + nombreBase + "/";

            // Create and write the content to the .txt file
            String contenido = String.format("{%s}\n{%s}\n{%d}\n{%s}\n{%s}",
                    titulo, texto, precio, talle, detalle);
            File archivoTxt = File.createTempFile("temp_", ".txt");
            Files.write(archivoTxt.toPath(), contenido.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Upload the .txt file and get its URL
            String rutaTxt = carpeta + nombreBase + ".txt";
            String urlTxt = subirArchivoAGoogleCloudStorage(rutaTxt, archivoTxt);

            // Upload the image and get its URL
            String extension = imagenSeleccionada.getName().substring(imagenSeleccionada.getName().lastIndexOf("."));
            String rutaImagen = carpeta + nombreBase + extension;
            String urlImagen = subirArchivoAGoogleCloudStorage(rutaImagen, imagenSeleccionada);

            // Update the products.json
            actualizarProductosJson(categoria, nombreBase, urlTxt, urlImagen);

            // Update the database to set Publicado_En_Web to "True"
            marcarProductoComoPublicado(idArticulo);

            // Clear fields and show success message
            limpiarCampos();
            imagenSeleccionada = null;
            lblImagenSeleccionada.setText("");
            mostrarAlerta("Éxito", "Artículo subido correctamente", Alert.AlertType.INFORMATION);

            obtenerProductosDesdeBD();
            tblProductosWeb.refresh();

        } catch (Exception e) {
            // Rollback: Delete the directory if an error occurs
            if (carpeta != null) {
                eliminarDirectorioEnGoogleCloud(carpeta);
            }
            mostrarAlerta("Error", "Error al generar y subir el artículo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void actualizarPrecioVentaTransferencia(int idArticulo, int precio) {
        conectarABBDDConReintentos();

        if (conexionABBDD != null) {
            String sql = "UPDATE productos SET precio_venta_transferencia = ? WHERE id_articulo = ?";
            try (PreparedStatement stmt = conexionABBDD.prepareStatement(sql)) {
                stmt.setInt(1, precio);
                stmt.setInt(2, idArticulo);
                stmt.executeUpdate();
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar el precio en la base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } finally {
                desconexionABBDD();
            }
        }
    }

    private void quitarDeLaWeb(RegistroArticuloVentas producto) {
        try {
            // Connect to Google Cloud Storage
            Storage storage = StorageOptions.getDefaultInstance().getService();

            // Define paths
            String folderName = producto.getIdArticulo() + "-" + producto.getNombreArticulo();
            String folderPath = "Novedades/" + folderName;
            String jsonFilePath = "productos.json";

            // Download productos.json
            Blob jsonBlob = storage.get("imagenes-web-capri", jsonFilePath);
            if (jsonBlob == null) {
                mostrarAlerta("Error", "El archivo productos.json no existe en el bucket.", Alert.AlertType.ERROR);
                return;
            }

            String jsonContent = new String(jsonBlob.getContent(), java.nio.charset.StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonContent);

            // Remove the node matching the id_articulo (only the number before the dash)
            boolean nodeRemoved = false;
            String idArticuloStr = String.valueOf(producto.getIdArticulo());
            String carpetaActual = null;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject productNode = jsonArray.getJSONObject(i);
                String carpeta = productNode.optString("carpeta");

                // Extract the id_articulo from the folder name (before the dash)
                if (carpeta != null && carpeta.contains("-")) {
                    String idEnCarpeta = carpeta.substring(0, carpeta.indexOf("-"));
                    if (idEnCarpeta.equals(idArticuloStr)) {
                        carpetaActual = carpeta; // Save the actual folder name from JSON
                        jsonArray.remove(i);
                        nodeRemoved = true;
                        break;
                    }
                }
            }

            if (!nodeRemoved) {
                mostrarAlerta("Error", "No se encontró el producto con ID " + idArticuloStr + " en productos.json.", Alert.AlertType.ERROR);
                return;
            }

            // Update folderPath with the actual folder name from JSON
            if (carpetaActual != null) {
                folderPath = "Novedades/" + carpetaActual;
            }

            // Upload the updated productos.json
            storage.create(BlobInfo.newBuilder("imagenes-web-capri", jsonFilePath)
                            .setContentType("application/json; charset=utf-8")
                            .build(),
                    jsonArray.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Delete the folder and its contents
            Page<Blob> blobs = storage.list("imagenes-web-capri", Storage.BlobListOption.prefix(folderPath));
            for (Blob blob : blobs.iterateAll()) {
                storage.delete(blob.getBlobId());
            }

            // Update the UI and database
            producto.setPublicadoEnWeb("false"); // Update the property in the UI
            marcarProductoComoNoPublicado(producto.getIdArticulo());
            // Refrescar la tabla
            obtenerProductosDesdeBD();
            tblProductosWeb.refresh();

            mostrarAlerta("Éxito", "El producto fue eliminado de la web correctamente.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo eliminar el producto de la web: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void marcarProductoComoNoPublicado(int idArticulo) {
        try {
            conectarABBDDConReintentos();
            String query = "UPDATE productos SET Publicado_En_Web = 'False' WHERE id_articulo = ?";
            PreparedStatement stmt = conexionABBDD.prepareStatement(query);
            stmt.setInt(1, idArticulo);
            stmt.executeUpdate();
            System.out.println("Producto marcado como no publicado en la web.");
        } catch (SQLException e) {
            System.err.println("Error al marcar el producto como no publicado: " + e.getMessage());
        } finally {
            desconexionABBDD();
        }
    }

    private void marcarProductoComoPublicado(int idArticulo) {
        try {
            conectarABBDDConReintentos();
            String query = "UPDATE productos SET Publicado_En_Web = 'True' WHERE id_articulo = ?";
            PreparedStatement stmt = conexionABBDD.prepareStatement(query);
            stmt.setInt(1, idArticulo);
            stmt.executeUpdate();
            System.out.println("Producto marcado como publicado en la web.");
        } catch (SQLException e) {
            System.err.println("Error al marcar el producto como publicado: " + e.getMessage());
        } finally {
            desconexionABBDD();
        }
    }

    private void eliminarDirectorioEnGoogleCloud(String carpeta) {
        try {
            Storage storage = StorageOptions.getDefaultInstance().getService();
            Page<Blob> blobs = storage.list("imagenes-web-capri", Storage.BlobListOption.prefix(carpeta));
            for (Blob blob : blobs.iterateAll()) {
                storage.delete(blob.getBlobId());
            }
            System.out.println("Directorio eliminado: " + carpeta);
        } catch (StorageException e) {
            System.err.println("Error al eliminar el directorio en Google Cloud Storage: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        fldNombreTarjeta.clear();
        fldTextoTarjeta.clear();
        fldPrecioTarjeta.clear();
        fldDetalleTarjeta.clear();
        cmbCategoriaWeb.setValue(null);
        chkNovedad.setSelected(false);
        tblProductosWeb.getSelectionModel().clearSelection();
    }

    private String subirArchivoAGoogleCloudStorage(String rutaDestino, File archivo) throws IOException {
        try {
            // Configuración de Google Cloud Storage
            Storage storage = StorageOptions.getDefaultInstance().getService();
            BlobId blobId = BlobId.of("imagenes-web-capri", rutaDestino);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(Files.probeContentType(archivo.toPath())).build();
            storage.create(blobInfo, Files.readAllBytes(archivo.toPath()));

            // Retornar la URL pública del archivo
            return "https://storage.googleapis.com/imagenes-web-capri/" + rutaDestino;
        } catch (StorageException e) {
            System.err.println("Error uploading file to Google Cloud Storage: " + e.getMessage());
            System.err.println("Code: " + e.getCode());
            System.err.println("Reason: " + e.getReason());
            throw new IOException("Error uploading file to Google Cloud Storage: " + e.getMessage(), e);
        }
    }

    private void actualizarProductosJson(String categoria, String carpeta, String urlTxt, String urlImagen) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get("imagenes-web-capri", "productos.json");

        JSONArray productos;

        try {
            // Check if the file exists
            if (blob == null || !blob.exists()) {
                System.out.println("El archivo productos.json no existe. Se creará uno nuevo.");
                productos = new JSONArray(); // Create a new JSON array
            } else {
                // Read the existing content
                String contenidoJson = new String(blob.getContent(), java.nio.charset.StandardCharsets.UTF_8);
                productos = contenidoJson.isEmpty() ? new JSONArray() : new JSONArray(contenidoJson);
            }

            // Modify category if novedad is selected
            if (chkNovedad.isSelected()) {
                categoria += "-Novedad";
            }

            // Add the new product
            JSONObject nuevoProducto = new JSONObject();
            nuevoProducto.put("categoria", categoria);
            nuevoProducto.put("carpeta", carpeta);
            nuevoProducto.put("imagen", urlImagen);
            nuevoProducto.put("txt", urlTxt);

            productos.put(nuevoProducto);

            // Write the updated JSON back to the bucket
            BlobId blobId = BlobId.of("imagenes-web-capri", "productos.json");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType("application/json; charset=utf-8")
                    .build();
            storage.create(blobInfo, productos.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));

            System.out.println("productos.json actualizado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar productos.json: " + e.getMessage());
            throw new IOException("Error al actualizar productos.json", e);
        }
    }
    ////////////PESTAÑA PEDIDOS ////////////////////////
    private void configurarTablaPedidos() {
        colIdPedido.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colFechaPedido.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colEstadoPedido.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colMontoPedido.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colNombreClientePedido.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));

        colAccionesPedido.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("Ver");
            {
                btnVer.setOnAction(event -> {
                    ObservableList<String> pedido = getTableRow().getItem();
                    mostrarVentanaDetallePedido(pedido);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnVer);
                }
            }
        });

        tblPedidos.setItems(listaPedidos);
    }

    private void refrescarTablaPedidos() {
        listaPedidos.clear();
        conectarABBDDConReintentos();

        if (conexionABBDD != null) {
            try {
                String sql = "SELECT * FROM sp_consultar_pedidos(?, ?, ?, ?)";
                PreparedStatement stmt = conexionABBDD.prepareStatement(sql);

                // Set parameters
                stmt.setString(1, fldBuscarArticuloPedido.getText());
                stmt.setString(2, fldBuscarEstadoPedido.getText());
                stmt.setDate(3, dtFechaInicialPedido.getValue() != null ?
                        Date.valueOf(dtFechaInicialPedido.getValue()) : null);
                stmt.setDate(4, dtFechaFinalPedido.getValue() != null ?
                        Date.valueOf(dtFechaFinalPedido.getValue()) : null);

                ResultSet rs = stmt.executeQuery();

                // Map pedidos by id_pedido to group items
                Map<String, ObservableList<String>> pedidosMap = new HashMap<>();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                while (rs.next()) {
                    String idPedido = rs.getString("id_pedido");

                    if (!pedidosMap.containsKey(idPedido)) {
                        ObservableList<String> fila = FXCollections.observableArrayList();
                        fila.add(idPedido); // ID Pedido

                        // Format the timestamp
                        Timestamp pedidoFecha = rs.getTimestamp("pedido_fecha");
                        String fechaFormateada = pedidoFecha != null ?
                                dateFormat.format(pedidoFecha) : "";
                        fila.add(fechaFormateada); // Fecha formateada

                        fila.add(rs.getString("estado")); // Estado
                        fila.add(rs.getString("pedido_monto_total")); // Monto Total
                        fila.add(rs.getString("pedido_nombre_cliente")); // Nombre Cliente

                        pedidosMap.put(idPedido, fila);
                    }
                }

                listaPedidos.addAll(pedidosMap.values());
                tblPedidos.refresh();

            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al cargar los pedidos: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } finally {
                desconexionABBDD();
            }
        }
    }
    private void mostrarVentanaDetallePedido(ObservableList<String> pedido) {
        Stage ventana = new Stage();
        ventana.setTitle("Detalle del Pedido");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(15));

        Label lblDetalle = new Label("Detalle del Pedido:");
        lblDetalle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        layout.getChildren().add(lblDetalle);

        GridPane gridResumen = new GridPane();
        gridResumen.setHgap(10);
        gridResumen.setVgap(5);
        String[] etiquetas = {"ID Pedido:", "Fecha:", "Estado:", "Monto:", "Cliente:"};
        for (int i = 0; i < etiquetas.length && i < pedido.size(); i++) {
            Label lblCampo = new Label(etiquetas[i]);
            lblCampo.setStyle("-fx-font-weight: bold;");
            gridResumen.add(lblCampo, 0, i);
            gridResumen.add(new Label(pedido.get(i)), 1, i);
        }
        layout.getChildren().add(gridResumen);

        Label lblArticulos = new Label("Artículos asociados:");
        lblArticulos.setStyle("-fx-font-weight: bold;");
        layout.getChildren().add(lblArticulos);

        TableView<ObservableList<String>> tablaArticulos = new TableView<>();
        tablaArticulos.setPrefHeight(250);
        tablaArticulos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaArticulos.setPlaceholder(new Label("No hay artículos asociados a este pedido."));

        String[] columnas = {"ID Artículo", "Prenda", "Categoría", "Color", "Talle", "Precio"};
        for (int i = 0; i < columnas.length; i++) {
            final int index = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnas[i]);
            col.setCellValueFactory(data -> {
                ObservableList<String> fila = data.getValue();
                return new SimpleStringProperty(index < fila.size() ? fila.get(index) : "");
            });
            tablaArticulos.getColumns().add(col);
        }

        ObservableList<ObservableList<String>> articulosPedido = obtenerArticulosDelPedido(pedido.get(0));
        if (articulosPedido.isEmpty()) {
            tablaArticulos.setPlaceholder(new Label("No se encontraron artículos para este pedido."));
        }
        tablaArticulos.setItems(articulosPedido);
        layout.getChildren().add(tablaArticulos);

        Button btnMarcarEntregado = new Button("Marcar como Entregado");
        btnMarcarEntregado.setOnAction(event -> {
            try {
                conectarABBDDConReintentos();
                if (conexionABBDD != null) {
                    String sql = "CALL sp_actualizar_pedido_entregado(?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setString(1, pedido.get(0));
                    stmt.execute();
                    desconexionABBDD();
                    refrescarTablaPedidos();
                    ventana.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        layout.getChildren().add(btnMarcarEntregado);

        Scene escena = new Scene(layout);
        ventana.setScene(escena);
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.showAndWait();
    }

    private ObservableList<ObservableList<String>> obtenerArticulosDelPedido(String codigoPedido) {
        ObservableList<ObservableList<String>> articulos = FXCollections.observableArrayList();
        conectarABBDDConReintentos();
        if (conexionABBDD == null) {
            return articulos;
        }

        String sql = "SELECT id_articulo, prenda, categoria, color, talle, precio_venta_transferencia " +
                "FROM productos WHERE id_pedido = ? ORDER BY id_articulo ASC";
        try (PreparedStatement stmt = conexionABBDD.prepareStatement(sql)) {
            stmt.setString(1, codigoPedido != null ? codigoPedido.trim() : null);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList(
                            rs.getString("id_articulo"),
                            rs.getString("prenda"),
                            rs.getString("categoria"),
                            rs.getString("color"),
                            rs.getString("talle"),
                            rs.getString("precio_venta_transferencia")
                    );
                    articulos.add(fila);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron obtener los artículos del pedido: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            desconexionABBDD();
        }
        return articulos;
    }

    private void cargarLista(String nombreLista) throws SQLException {
        listaActual = nombreLista; // Guardamos la lista actual
        conectarABBDDConReintentos();
        if (conexionABBDD != null) {
            String sql = "SELECT * FROM sp_consultar_lista(?)";
            PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
            stmt.setString(1, nombreLista);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Array colNamesArray = rs.getArray("col_names");
                String[] columnas = colNamesArray != null ? (String[]) colNamesArray.getArray() : new String[0];

                String registrosJson = rs.getString("registros");
                JSONArray registros = (registrosJson != null) ? new JSONArray(registrosJson) : new JSONArray();

                actualizarTabla(columnas, registros);
            } else {
                actualizarTabla(new String[0], new JSONArray());
            }
        }
    }

    private void actualizarTabla(String[] columnas, JSONArray registros) {
        tblConsulta.getColumns().clear(); // Borra columnas anteriores
        tblConsulta.getItems().clear();   // Borra datos anteriores

        // Crear las columnas respetando el orden original de 'columnas'
        for (int i = 0; i < columnas.length; i++) {
            final int index = i;
            String nombreOriginal = columnas[i];
            String nombreTransformado = nombreOriginal.replace("_", " ").toUpperCase();

            TableColumn<ObservableList<String>, String> col = new TableColumn<>(nombreTransformado);
            col.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get(index))
            );
            tblConsulta.getColumns().add(col);
        }

        // Cargar los datos en el mismo orden que las columnas
        ObservableList<ObservableList<String>> datos = FXCollections.observableArrayList();
        for (int i = 0; i < registros.length(); i++) {
            JSONObject obj = registros.getJSONObject(i);
            ObservableList<String> fila = FXCollections.observableArrayList();

            for (String columna : columnas) {
                fila.add(obj.optString(columna, ""));
            }
            datos.add(fila);
        }
        tblConsulta.setItems(datos);

        // Agregar columna de Acciones
        TableColumn<ObservableList<String>, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(5, btnModificar, btnEliminar);

            {
                btnModificar.setOnAction(event -> {
                    ObservableList<String> fila = getTableView().getItems().get(getIndex());
                    mostrarVentanaModificacion(columnas, fila,tblConsulta);
                });

                btnEliminar.setOnAction(event -> {
                    ObservableList<String> fila = getTableView().getItems().get(getIndex());
                    mostrarVentanaEliminar(List.of(columnas), fila,tblConsulta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
        tblConsulta.getColumns().add(colAcciones);
    }

    public void mostrarVentanaModificacion(String[] columnas, ObservableList<String> fila,TableView<ObservableList<String>> tabla) {
        Stage ventana = new Stage();
        ventana.setTitle("Modificar Registro");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Map<String, TextField> camposTexto = new LinkedHashMap<>();

        for (int i = 0; i < columnas.length; i++) {
            String columna = columnas[i];
            TextField campo = new TextField(fila.get(i));
            campo.setPromptText(columna.replace("_", " ").toUpperCase());
            if (columna.toLowerCase().startsWith("id_")) {
                campo.setDisable(true); // 🔒 Deshabilita el campo si es ID
            }
            camposTexto.put(columna, campo);
            grid.add(new Label(columna.replace("_", " ").toUpperCase()), 0, i);
            grid.add(campo, 1, i);
        }


        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        Button btnGuardar = new Button("Guardar Cambios");
        Button btnEliminar = new Button("Eliminar Registro");
        Button btnCancelar = new Button("Cancelar");

        botones.getChildren().addAll(btnGuardar, btnEliminar, btnCancelar);
        grid.add(botones, 0, columnas.length, 2, 1);

        btnGuardar.setOnAction(e -> {
            try {
                Map<String, String> datos = new HashMap<>();
                for (Map.Entry<String, TextField> entry : camposTexto.entrySet()) {
                    datos.put(entry.getKey(), entry.getValue().getText());
                }

                // Validar si hay un campo ID
                Optional<String> campoID = datos.keySet().stream().filter(k -> k.toLowerCase().startsWith("id_")).findFirst();
                if (!campoID.isPresent()) {
                    mostrarAlerta("Error al guardar cambios", "No se encontró el campo ID en los datos.", Alert.AlertType.ERROR);
                    return;
                }
                ejecutarModificacionEnBBDD(datos);
                mostrarAlerta("Actualización", "Se actualizó el registro correctamente", Alert.AlertType.INFORMATION);
                String seleccionActual = cmbListas.getValue();
                // 🟡 Actualizar visualmente la fila modificada en la tabla
                for (int i = 0; i < columnas.length; i++) {
                    fila.set(i, camposTexto.get(columnas[i]).getText());
                }
                ventana.close();
                tabla.refresh();
            } catch (Exception ex) {
                mostrarAlerta("Error al guardar", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnEliminar.setOnAction(e -> {
            try {
                Map<String, String> datos = new HashMap<>();
                for (Map.Entry<String, TextField> entry : camposTexto.entrySet()) {
                    datos.put(entry.getKey(), entry.getValue().getText());
                }

                Optional<String> campoID = datos.keySet().stream().filter(k -> k.toLowerCase().startsWith("id_")).findFirst();
                if (!campoID.isPresent()) {
                    mostrarAlerta("Error al eliminar", "No se encontró el campo ID en los datos.", Alert.AlertType.ERROR);
                    return;
                }
                ejecutarEliminacionEnBBDD(datos);
                mostrarAlerta("Eliminación", "Se eliminó el registro correctamente", Alert.AlertType.INFORMATION);
                String seleccionActual = cmbListas.getValue();
                // 🟡 Actualizar visualmente la fila modificada en la tabla
                for (int i = 0; i < columnas.length; i++) {
                    fila.set(i, camposTexto.get(columnas[i]).getText());
                }
                ventana.close();
                tabla.refresh();

            } catch (Exception ex) {
                mostrarAlerta("Error al eliminar", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnCancelar.setOnAction(e -> ventana.close());

        Scene scene = new Scene(grid);
        ventana.setScene(scene);
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.showAndWait();
    }

    public void mostrarVentanaEliminar(List<String> columnas, List<String> fila,TableView<ObservableList<String>> tabla) {
        Stage ventana = new Stage();
        ventana.setTitle("Confirmar Eliminación");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label mensaje = new Label("¿Estás seguro de eliminar este registro?");
        layout.getChildren().add(mensaje);

        Map<String, String> datos = new HashMap<>();
        for (int i = 0; i < columnas.size(); i++) {
            String campo = columnas.get(i);
            String valor = fila.get(i);
            datos.put(campo, valor);
            layout.getChildren().add(new Label(campo.replace("_", " ") + ": " + valor));
        }

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        Button btnConfirmar = new Button("Eliminar");
        Button btnCancelar = new Button("Cancelar");

        botones.getChildren().addAll(btnCancelar, btnConfirmar);
        layout.getChildren().add(botones);

        btnCancelar.setOnAction(e -> ventana.close());

        btnConfirmar.setOnAction(e -> {
            try {
                ejecutarEliminacionEnBBDD(datos);
                ventana.close();
                refrescarTablaActual();
                mostrarAlerta("Eliminación", "Se eliminó el registro correctamente", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error al eliminar","Error al eliminar: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Scene escena = new Scene(layout);
        ventana.setScene(escena);
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.showAndWait();
    }

    private void ejecutarModificacionEnBBDD(Map<String, String> datos) throws SQLException {
        conectarABBDDConReintentos();
        if (conexionABBDD != null) {
            String nombreTabla = cmbListas.getValue().toLowerCase();

            // Se asume que el ID está siempre con nombre 'id_X', ejemplo: id_producto, id_cliente, etc.
            String nombreId = datos.keySet().stream()
                    .filter(k -> k.toLowerCase().startsWith("id_"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró el campo ID en los datos."));

            int valorId = Integer.parseInt(datos.get(nombreId));

            // Quitar el campo ID del JSON para actualizar solo los otros campos
            Map<String, String> camposSinId = new HashMap<>(datos);
            camposSinId.remove(nombreId);
            String sql = "CALL public.sp_modificar_registro(?, ?, ?, ?)";
            PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
            stmt.setString(1, nombreTabla);
            stmt.setString(2, nombreId);
            stmt.setInt(3, valorId);
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(new JSONObject(camposSinId).toString());
            stmt.setObject(4, jsonObject);

            stmt.execute();
        }
        desconexionABBDD();
    }

    private void ejecutarEliminacionEnBBDD(Map<String, String> datos) throws SQLException {
        conectarABBDDConReintentos();
        if (conexionABBDD != null) {
            String nombreTabla = cmbListas.getValue();

            String nombreId = datos.keySet().stream()
                    .filter(k -> k.toLowerCase().startsWith("id_"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró el campo ID en los datos."));

            int valorId = Integer.parseInt(datos.get(nombreId));
            String sql = "call sp_eliminar_registro(?, ?, ?) ";
            PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
            stmt.setString(1, nombreTabla);
            stmt.setString(2, nombreId);
            stmt.setInt(3, valorId);

            stmt.execute();
        }
        desconexionABBDD();
    }

    private void refrescarTablaActual() {
        if (listaActual != null) {
            try {
                cargarLista(listaActual);
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error al refrescar la tabla", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }



    private String toCamelCase(String text) {
        String[] parts = text.split("_");
        StringBuilder camelCase = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                camelCase.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase()).append(" ");
            }
        }
        return camelCase.toString().trim();
    }


    //////////// FUNCIONES PESTAÑA CARGA ////////////////////////
    private void buscarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        // Abre el cuadro de diálogo y almacena el archivo seleccionado
        archivoSeleccionado = fileChooser.showOpenDialog(new Stage());

        if (archivoSeleccionado != null) {
            // Muestra la ruta del archivo en la etiqueta y habilita el botón de carga
            rutaArchivoCargado.setText(archivoSeleccionado.getAbsolutePath());
            btnCargarArchivo.setDisable(false);
        }
    }

    private void cargarArchivo() throws IOException {
        if (archivoSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación de Carga");
            confirmacion.setHeaderText("Se cargará archivo seleccionado");
            confirmacion.setContentText("¿Seguro desea continuar?");
            Optional<ButtonType> resultado = confirmacion.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                System.out.println("Cargando el archivo: " + archivoSeleccionado.getAbsolutePath());
                cargarExcelABBDD(archivoSeleccionado);

                initialize(null, null);  // Reinicia el controlador o realiza otra acción al finalizar la carga
            } else {
                // Si el usuario cancela, no se realiza ninguna acción
                System.out.println("Carga de archivo cancelada por el usuario.");
            }
        }
    }

    private void cargarExcelABBDD(File in_ArchivoSeleccionado) throws IOException {
        lstLotes = new ArrayList<>();
        lstProductos = new ArrayList<>();
        lstPagos = new ArrayList<>();
        lstGastosExtra = new ArrayList<>();
        lstClientes = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(in_ArchivoSeleccionado);
            Workbook workbook = new XSSFWorkbook(fis)) {
            //--------------------
            Sheet hojaLotes = workbook.getSheet("Lotes");
            if (hojaLotes != null) {
                System.out.println("Estoy procesando >>Lotes<<");
                cargarListaLotes(hojaLotes);
                conectarABBDDConReintentos();
                cargarListaLotesABBDD(lstLotes);
                desconexionABBDD();
            } else {
                System.out.println("Hoja 'Lotes' no encontrada, saltando...");
            }
            //-----------------------
            Sheet hojaProductos = workbook.getSheet("Productos");
            if (hojaProductos != null) {
                System.out.println("Estoy procesando >>Productos<<");
                cargarListaProductos(hojaProductos);
                conectarABBDDConReintentos();
                cargarListaProductosABBDD(lstProductos);
                desconexionABBDD();
            } else {
                System.out.println("Hoja 'Productos' no encontrada, saltando...");
            }
            //-----------------------
            Sheet hojaPagos = workbook.getSheet("Pagos");
            if (hojaPagos != null) {
                System.out.println("Estoy procesando >>Pagos<<");
                cargarListaPagos(hojaPagos);
                conectarABBDDConReintentos();
                cargarListaPagosABBDD(lstPagos);
                desconexionABBDD();
            } else {
                System.out.println("Hoja 'Pagos' no encontrada, saltando...");
            }
            //-----------------------
            Sheet hojaGastosExtra = workbook.getSheet("Gastos Extra");
            if (hojaGastosExtra != null) {
                System.out.println("Estoy procesando >>Gastos Extra<<");
                cargarListaGastosExtra(hojaGastosExtra);
                conectarABBDDConReintentos();
                cargarListaGastosExtraABBDD(lstGastosExtra);
                desconexionABBDD();
            } else {
                System.out.println("Hoja 'Gastos Extra' no encontrada, saltando...");
            }
            //-----------------------
            Sheet hojaClientes = workbook.getSheet("Clientes");
            if (hojaClientes != null) {
                System.out.println("Estoy procesando >>Clientes<<");
                cargarListaClientes(hojaClientes);
                conectarABBDDConReintentos();
                cargarListaClientesABBDD(lstClientes);
                desconexionABBDD();
            } else {
                System.out.println("Hoja 'Clientes' no encontrada, saltando...");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void resetearCargaArchivo() {
        btnBuscarArchivo.setDisable(false);
        rutaArchivoCargado.setText("");
        btnCargarArchivo.setDisable(true);
    }

    /**
     * Método helper para obtener el valor de una celda como String,
     * manejando diferentes tipos de celda (NUMERIC, STRING, FORMULA, etc.)
     */
    private String obtenerValorCeldaComoString(Cell celda) {
        if (celda == null) {
            return "";
        }

        switch (celda.getCellType()) {
            case STRING:
                return celda.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(celda)) {
                    // Si es una fecha, formatearla como String
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    return sdf.format(celda.getDateCellValue());
                } else {
                    // Si es un número, convertirlo a String
                    double valor = celda.getNumericCellValue();
                    // Si es un número entero, no mostrar decimales
                    if (valor == (long) valor) {
                        return String.valueOf((long) valor);
                    } else {
                        return String.valueOf(valor);
                    }
                }
            case BOOLEAN:
                return String.valueOf(celda.getBooleanCellValue());
            case FORMULA:
                // Para fórmulas, intentar obtener el valor calculado
                try {
                    return celda.getStringCellValue();
                } catch (IllegalStateException e) {
                    try {
                        double valor = celda.getNumericCellValue();
                        if (valor == (long) valor) {
                            return String.valueOf((long) valor);
                        } else {
                            return String.valueOf(valor);
                        }
                    } catch (IllegalStateException ex) {
                        return "";
                    }
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private void cargarListaLotes(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                if (fila == null) continue;

                Cell celdaIdLote = fila.getCell(0);
                Cell celdaFecha = fila.getCell(1);
                Cell celdaCosto = fila.getCell(2);
                Cell celdaLocal = fila.getCell(3);
                Cell celdaInversor = fila.getCell(4);

                // Validar que las celdas requeridas no sean null
                if (celdaIdLote == null || celdaFecha == null || celdaCosto == null) {
                    continue;
                }

                // Obtener valores manejando correctamente los tipos de celda
                int idLote = 0;
                try {
                    if (celdaIdLote.getCellType() == CellType.NUMERIC) {
                        idLote = (int) celdaIdLote.getNumericCellValue();
                    } else {
                        idLote = Integer.parseInt(obtenerValorCeldaComoString(celdaIdLote));
                    }
                } catch (Exception e) {
                    System.out.println("Error al leer id_lote en fila " + (i+1) + ": " + e.getMessage());
                    continue;
                }

                String fecha = obtenerValorCeldaComoString(celdaFecha);
                fecha = (fecha == null || fecha.trim().isEmpty()) ? null : fecha.trim();

                double costo = 0;
                try {
                    if (celdaCosto.getCellType() == CellType.NUMERIC) {
                        costo = celdaCosto.getNumericCellValue();
                    } else {
                        costo = Double.parseDouble(obtenerValorCeldaComoString(celdaCosto));
                    }
                } catch (Exception e) {
                    System.out.println("Error al leer costo en fila " + (i+1) + ": " + e.getMessage());
                    continue;
                }

                String local = obtenerValorCeldaComoString(celdaLocal);
                local = (local == null || local.trim().isEmpty()) ? null : local.trim();

                String inversor = obtenerValorCeldaComoString(celdaInversor);
                inversor = (inversor == null || inversor.trim().isEmpty()) ? null : inversor.trim();

                RegistroLote registroLote = new RegistroLote(idLote, fecha, costo, local, inversor);
                lstLotes.add(registroLote);
            }
        }
    }

    private void cargarListaProductos(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        if (filaPrimerDato == null) {
            System.out.println("No hay datos en la hoja Productos");
            return;
        }

        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                if (fila == null) continue;

                // Formato Excel: ID (col 0 - se ignora, autoincremental en BD) | Estado | Prenda | Categoria | Color | Talle | Precio Compra | Precio Venta Efectivo | Precio Venta Transferencia | Id Lote
                // Saltamos la columna 0 (ID) porque es autoincremental en la base de datos
                Cell celdaEstado = fila.getCell(1);  // Antes era 0, ahora 1

                // Validar que la fila tenga datos (al menos el estado)
                if (celdaEstado == null || celdaEstado.toString().trim().isEmpty()) {
                    continue;
                }

                Cell celdaNombrePrenda = fila.getCell(2);  // Antes era 1, ahora 2
                Cell celdaCategoria = fila.getCell(3);     // Antes era 2, ahora 3
                Cell celdaColor = fila.getCell(4);         // Antes era 3, ahora 4
                Cell celdaTalle = fila.getCell(5);         // Antes era 4, ahora 5
                Cell celdaPrecioCompra = fila.getCell(6);  // Antes era 5, ahora 6
                Cell celdaPrecioVentaEfectivo = fila.getCell(7);  // Antes era 6, ahora 7
                Cell celdaPrecioVentaTransf = fila.getCell(8);    // Antes era 7, ahora 8
                Cell celdaId_Lote = fila.getCell(9);       // Antes era 8, ahora 9

                // Validar que las celdas requeridas no sean null
                // Según el esquema: categoria, talle, precio_compra, precio_venta_efectivo, precio_venta_transferencia son NOT NULL
                if (celdaCategoria == null || celdaTalle == null || celdaPrecioCompra == null ||
                    celdaPrecioVentaEfectivo == null || celdaPrecioVentaTransf == null) {
                    System.out.println("Fila " + (i+1) + " tiene celdas requeridas vacías (categoría, talle o precios), saltando...");
                    continue;
                }

                try {
                    // Obtener valores de texto (pueden ser null si están vacíos)
                    String estado = obtenerValorCeldaComoString(celdaEstado);
                    estado = (estado == null || estado.trim().isEmpty()) ? null : estado.trim();

                    String nombrePrenda = obtenerValorCeldaComoString(celdaNombrePrenda);
                    nombrePrenda = (nombrePrenda == null || nombrePrenda.trim().isEmpty()) ? null : nombrePrenda.trim();

                    String categoria = obtenerValorCeldaComoString(celdaCategoria);
                    categoria = (categoria == null || categoria.trim().isEmpty()) ? null : categoria.trim();

                    String color = obtenerValorCeldaComoString(celdaColor);
                    color = (color == null || color.trim().isEmpty()) ? null : color.trim();

                    String talle = obtenerValorCeldaComoString(celdaTalle);
                    talle = (talle == null || talle.trim().isEmpty()) ? null : talle.trim();

                    System.out.println("Fila " + (i+1) + ": " + estado + " | " + nombrePrenda + " | " + categoria + " | " + color + " | " + talle);

                    // Parsear Precio Compra con validación (null si no hay valor válido)
                    Double precioCompra = null;
                    String precioCompraStr = obtenerValorCeldaComoString(celdaPrecioCompra);
                    try {
                        if (celdaPrecioCompra.getCellType() == CellType.NUMERIC) {
                            precioCompra = celdaPrecioCompra.getNumericCellValue();
                        } else if (precioCompraStr != null && !precioCompraStr.trim().isEmpty()) {
                            precioCompraStr = precioCompraStr.trim().replace(",", ".");
                            precioCompra = Double.parseDouble(precioCompraStr);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error en fila " + (i+1) + ": No se pudo convertir precio_compra '" + precioCompraStr + "' a número. Usando NULL.");
                        precioCompra = null;
                    }

                    // Parsear Precio Venta Efectivo con validación (null si no hay valor válido)
                    Double precioVentaEfectivo = null;
                    String precioEfectivoStr = obtenerValorCeldaComoString(celdaPrecioVentaEfectivo);
                    try {
                        if (celdaPrecioVentaEfectivo.getCellType() == CellType.NUMERIC) {
                            precioVentaEfectivo = celdaPrecioVentaEfectivo.getNumericCellValue();
                        } else if (precioEfectivoStr != null && !precioEfectivoStr.trim().isEmpty()) {
                            precioEfectivoStr = precioEfectivoStr.trim().replace(",", ".");
                            precioVentaEfectivo = Double.parseDouble(precioEfectivoStr);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error en fila " + (i+1) + ": No se pudo convertir precio_venta_efectivo '" + precioEfectivoStr + "' a número. Usando NULL.");
                        precioVentaEfectivo = null;
                    }

                    // Parsear Precio Venta Transferencia con validación (null si no hay valor válido)
                    Double precioVentaTransf = null;
                    String precioTransfStr = obtenerValorCeldaComoString(celdaPrecioVentaTransf);
                    try {
                        if (celdaPrecioVentaTransf.getCellType() == CellType.NUMERIC) {
                            precioVentaTransf = celdaPrecioVentaTransf.getNumericCellValue();
                        } else if (precioTransfStr != null && !precioTransfStr.trim().isEmpty()) {
                            precioTransfStr = precioTransfStr.trim().replace(",", ".");
                            precioVentaTransf = Double.parseDouble(precioTransfStr);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error en fila " + (i+1) + ": No se pudo convertir precio_venta_transf '" + precioTransfStr + "' a número. Usando NULL.");
                        precioVentaTransf = null;
                    }

                    System.out.println("  Precios: Compra=" + precioCompra + " | Efectivo=" + precioVentaEfectivo + " | Transf=" + precioVentaTransf);

                    // Id_Lote puede ser null o vacío
                    int idLote = 0;
                    if (celdaId_Lote != null && celdaId_Lote.getCellType() != CellType.BLANK) {
                        try {
                            if (celdaId_Lote.getCellType() == CellType.NUMERIC) {
                                idLote = (int) celdaId_Lote.getNumericCellValue();
                            } else {
                                idLote = Integer.parseInt(obtenerValorCeldaComoString(celdaId_Lote));
                            }
                            System.out.println("Id Lote: " + idLote);
                        } catch (Exception e) {
                            System.out.println("Id Lote vacío o inválido, usando 0");
                        }
                    }

                    // Crear el registro usando 0 como valor por defecto para precios null (requerido por el constructor)
                    // Pero guardaremos la información de si era null para insertar NULL en la BD
                    RegistroProducto registroProducto = new RegistroProducto(
                            estado,
                            nombrePrenda,
                            categoria,
                            color,
                            talle,
                            precioCompra != null ? precioCompra : 0.0,
                            precioVentaEfectivo != null ? precioVentaEfectivo : 0.0,
                            precioVentaTransf != null ? precioVentaTransf : 0.0,
                            idLote);

                    // Establecer los valores (manteniendo los null en memoria para la lógica de inserción)
                    registroProducto.setEstado(estado);
                    registroProducto.setNombrePrenda(nombrePrenda);
                    registroProducto.setCategoria(categoria);
                    registroProducto.setColor(color);
                    registroProducto.setTalle(talle);
                    registroProducto.setPrecioCompra(precioCompra != null ? precioCompra : 0.0);
                    registroProducto.setPrecioVentaEfectivo(precioVentaEfectivo != null ? precioVentaEfectivo : 0.0);
                    registroProducto.setPrecioVentaTransf(precioVentaTransf != null ? precioVentaTransf : 0.0);
                    System.out.println("-----------------------------------------");

                    lstProductos.add(registroProducto);
                } catch (Exception e) {
                    System.out.println("Error procesando fila " + i + ": " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }

    private void cargarListaPagos(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                if (fila == null) continue;

                Cell celdaFecha = fila.getCell(0);
                Cell celdaMonto = fila.getCell(1);
                Cell celdaNombreCliente = fila.getCell(2);
                Cell celdaMetodoPago = fila.getCell(3);

                if (celdaFecha == null || celdaMonto == null) {
                    continue;
                }

                String fecha = obtenerValorCeldaComoString(celdaFecha);
                fecha = (fecha == null || fecha.trim().isEmpty()) ? null : fecha.trim();

                double monto = 0;
                try {
                    if (celdaMonto.getCellType() == CellType.NUMERIC) {
                        monto = celdaMonto.getNumericCellValue();
                    } else {
                        monto = Double.parseDouble(obtenerValorCeldaComoString(celdaMonto));
                    }
                } catch (Exception e) {
                    System.out.println("Error al leer monto en fila " + (i+1) + ": " + e.getMessage());
                    continue;
                }

                String nombreCliente = obtenerValorCeldaComoString(celdaNombreCliente);
                nombreCliente = (nombreCliente == null || nombreCliente.trim().isEmpty()) ? null : nombreCliente.trim();

                String metodoPago = obtenerValorCeldaComoString(celdaMetodoPago);
                metodoPago = (metodoPago == null || metodoPago.trim().isEmpty()) ? null : metodoPago.trim();

                RegistroPago registroPago = new RegistroPago(fecha, monto, nombreCliente, metodoPago);
                lstPagos.add(registroPago);
            }
        }
    }

    private void cargarListaGastosExtra(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                if (fila == null) continue;

                Cell celdaFecha = fila.getCell(0);
                Cell celdaCosto = fila.getCell(1);
                Cell celdaInversor = fila.getCell(2);
                Cell celdaObservacion = fila.getCell(3);

                if (celdaFecha == null || celdaCosto == null) {
                    continue;
                }

                String fecha = obtenerValorCeldaComoString(celdaFecha);
                fecha = (fecha == null || fecha.trim().isEmpty()) ? null : fecha.trim();

                double costo = 0;
                try {
                    if (celdaCosto.getCellType() == CellType.NUMERIC) {
                        costo = celdaCosto.getNumericCellValue();
                    } else {
                        costo = Double.parseDouble(obtenerValorCeldaComoString(celdaCosto));
                    }
                } catch (Exception e) {
                    System.out.println("Error al leer costo en fila " + (i+1) + ": " + e.getMessage());
                    continue;
                }

                String inversor = obtenerValorCeldaComoString(celdaInversor);
                inversor = (inversor == null || inversor.trim().isEmpty()) ? null : inversor.trim();

                String observacion = obtenerValorCeldaComoString(celdaObservacion);
                observacion = (observacion == null || observacion.trim().isEmpty()) ? null : observacion.trim();

                RegistroGastoExtra registroGastoExtra = new RegistroGastoExtra(fecha, costo, inversor, observacion);
                lstGastosExtra.add(registroGastoExtra);
            }
        }
    }

    private void cargarListaClientes(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                if (fila == null) continue;

                Cell celdaNombre = fila.getCell(0);
                Cell celdaCorreo = fila.getCell(1);
                Cell celdaInstagram = fila.getCell(2);

                if (celdaNombre == null) {
                    continue;
                }

                String nombre = obtenerValorCeldaComoString(celdaNombre);
                String correo = obtenerValorCeldaComoString(celdaCorreo);
                String instagram = obtenerValorCeldaComoString(celdaInstagram);

                RegistroCliente registroClientes = new RegistroCliente(nombre, correo, instagram);
                lstClientes.add(registroClientes);
            }
        }
    }

    private void cargarListaLotesABBDD(List<RegistroLote> listaLotes){
        if (conexionABBDD != null) {
            try {

                for (int i = 0; i < listaLotes.size(); i++) {
                    RegistroLote lote = listaLotes.get(i);

                    // Validar que los campos requeridos no sean nulos
                    Timestamp fechaTimestamp = convertirStringATimestamp(lote.getFecha());
                    if (fechaTimestamp == null) {
                        System.err.println("Error: Fecha nula o inválida para el lote " + lote.getId_lote() + ". Saltando este lote.");
                        continue;
                    }

                    String sql = "CALL public.sp_inserta_lotes(?, ?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setTimestamp(1, fechaTimestamp);
                    stmt.setDouble(2, lote.getCosto());

                    // Insertar Local como NULL si está vacío
                    String local = lote.getLocal();
                    if (local == null || local.trim().isEmpty()) {
                        stmt.setNull(3, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(3, local);
                    }

                    // Insertar Inversor como NULL si está vacío
                    String inversor = lote.getInversor();
                    if (inversor == null || inversor.trim().isEmpty()) {
                        stmt.setNull(4, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(4, inversor);
                    }

                    System.out.println("Insertando lote:");
                    System.out.println("  Fecha: " + fechaTimestamp);
                    System.out.println("  Costo: " + lote.getCosto());
                    System.out.println("  Local: " + lote.getLocal());
                    System.out.println("  Inversor: " + lote.getInversor());
                    System.out.println("-----------------------------");

                    stmt.execute();
                    System.out.println("Lote insertado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cargarListaProductosABBDD(List<RegistroProducto> listaProductos){
        if (conexionABBDD != null) {
            try {
                for (int i = 0; i < listaProductos.size(); i++) {
                    String sql = "CALL public.sp_inserta_productos(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);

                    RegistroProducto producto = listaProductos.get(i);

                    // Insertar campos String como NULL si están vacíos
                    if (producto.getEstado() == null || producto.getEstado().trim().isEmpty()) {
                        stmt.setNull(1, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(1, producto.getEstado());
                    }

                    if (producto.getNombrePrenda() == null || producto.getNombrePrenda().trim().isEmpty()) {
                        stmt.setNull(2, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(2, producto.getNombrePrenda());
                    }

                    if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
                        stmt.setNull(3, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(3, producto.getCategoria());
                    }

                    if (producto.getColor() == null || producto.getColor().trim().isEmpty()) {
                        stmt.setNull(4, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(4, producto.getColor());
                    }

                    if (producto.getTalle() == null || producto.getTalle().trim().isEmpty()) {
                        stmt.setNull(5, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(5, producto.getTalle());
                    }

                    // Los precios son campos NOT NULL en la base de datos, por lo tanto siempre deben tener un valor
                    // Si son 0, significa que no se proporcionaron correctamente y debemos saltar este registro
                    if (producto.getPrecioCompra() == 0.0 || producto.getPrecioVentaEfectivo() == 0.0 || producto.getPrecioVentaTransf() == 0.0) {
                        System.err.println("Error: Producto con precios inválidos (0 o null) en fila. Saltando este registro.");
                        System.err.println("  Prenda: " + producto.getNombrePrenda());
                        System.err.println("  Precios: Compra=" + producto.getPrecioCompra() + " | Efectivo=" + producto.getPrecioVentaEfectivo() + " | Transf=" + producto.getPrecioVentaTransf());
                        stmt.close();
                        continue;
                    }

                    stmt.setDouble(6, producto.getPrecioCompra());
                    stmt.setDouble(7, producto.getPrecioVentaEfectivo());
                    stmt.setDouble(8, producto.getPrecioVentaTransf());

                    // Si id_lote es 0, enviar NULL a la base de datos
                    int idLote = producto.getId_lote();
                    if (idLote == 0) {
                        stmt.setNull(9, java.sql.Types.INTEGER);
                    } else {
                        stmt.setInt(9, idLote);
                    }

                    System.out.println("------------------Cargando a bd---------------------");
                    System.out.println(listaProductos.get(i).getEstado());
                    System.out.println(listaProductos.get(i).getNombrePrenda());
                    System.out.println(listaProductos.get(i).getCategoria());
                    System.out.println(listaProductos.get(i).getColor());
                    System.out.println(listaProductos.get(i).getTalle());
                    System.out.println(listaProductos.get(i).getEstado());
                    System.out.println(listaProductos.get(i).getPrecioVentaEfectivo());
                    System.out.println(listaProductos.get(i).getPrecioVentaTransf());
                    System.out.println("-----------------------------");
                    stmt.execute();
                    System.out.println("Procedimiento almacenado ejecutado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
            refrescarTablaStock();
        }
    }

    private void cargarListaPagosABBDD(List<RegistroPago> listaPagos){
        if (conexionABBDD != null) {
            try {

                for (int i = 0; i < listaPagos.size(); i++) {
                    RegistroPago pago = listaPagos.get(i);

                    String sql = "CALL public.sp_inserta_pagos(?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setDouble(1, pago.getMonto());

                    // Insertar NombreCliente como NULL si está vacío
                    String nombreCliente = pago.getNombreCliente();
                    if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
                        stmt.setNull(2, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(2, nombreCliente);
                    }

                    // Insertar MetodoPago como NULL si está vacío
                    String metodoPago = pago.getMetodoPago();
                    if (metodoPago == null || metodoPago.trim().isEmpty()) {
                        stmt.setNull(3, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(3, metodoPago);
                    }

                    System.out.println(listaPagos.get(i).getMonto());
                    System.out.println(String.valueOf(listaPagos.get(i).getNombreCliente()));
                    System.out.println(String.valueOf(listaPagos.get(i).getMetodoPago()));
                    System.out.println("-----------------------------");


                    stmt.execute();
                    System.out.println("Procedimiento almacenado "+ sql + " ejecutado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cargarListaGastosExtraABBDD(List<RegistroGastoExtra> listaGastoExtra){
        if (conexionABBDD != null) {
            try {

                for (int i = 0; i < listaGastoExtra.size(); i++) {
                    RegistroGastoExtra gasto = listaGastoExtra.get(i);

                    // Validar que los campos requeridos no sean nulos
                    Timestamp fechaTimestamp = convertirStringATimestamp(gasto.getFecha());
                    if (fechaTimestamp == null) {
                        System.err.println("Error: Fecha nula o inválida para el gasto extra. Saltando este registro.");
                        continue;
                    }

                    String sql = "CALL public.sp_inserta_gasto_extra(?, ?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setTimestamp(1, fechaTimestamp);
                    stmt.setDouble(2, gasto.getMonto());

                    // Insertar Inversor como NULL si está vacío
                    String inversor = gasto.getInversor();
                    if (inversor == null || inversor.trim().isEmpty()) {
                        stmt.setNull(3, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(3, inversor);
                    }

                    // Insertar Observación como NULL si está vacío
                    String observacion = gasto.getObservacion();
                    if (observacion == null || observacion.trim().isEmpty()) {
                        stmt.setNull(4, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(4, observacion);
                    }

                    System.out.println("Insertando gasto extra:");
                    System.out.println("  Fecha: " + fechaTimestamp);
                    System.out.println("  Monto: " + gasto.getMonto());
                    System.out.println("  Inversor: " + gasto.getInversor());
                    System.out.println("  Observación: " + gasto.getObservacion());
                    System.out.println("-----------------------------");

                    stmt.execute();
                    System.out.println("Gasto extra insertado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cargarListaClientesABBDD(List<RegistroCliente> listaClientes){
        if (conexionABBDD != null) {
            try {

                for (int i = 0; i < listaClientes.size(); i++) {
                    RegistroCliente cliente = listaClientes.get(i);

                    String sql = "CALL public.sp_inserta_clientes(?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);

                    // Insertar Nombre como NULL si está vacío
                    String nombre = cliente.getNombre();
                    if (nombre == null || nombre.trim().isEmpty()) {
                        stmt.setNull(1, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(1, nombre);
                    }

                    // Insertar Correo como NULL si está vacío
                    String correo = cliente.getCorreo();
                    if (correo == null || correo.trim().isEmpty()) {
                        stmt.setNull(2, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(2, correo);
                    }

                    // Insertar Instagram como NULL si está vacío
                    String instagram = cliente.getInstagram();
                    if (instagram == null || instagram.trim().isEmpty()) {
                        stmt.setNull(3, java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString(3, instagram);
                    }

                    System.out.println(listaClientes.get(i).getNombre());
                    System.out.println(listaClientes.get(i).getCorreo());
                    System.out.println(listaClientes.get(i).getInstagram());

                    System.out.println("-----------------------------");

                    stmt.execute();
                    System.out.println("Procedimiento almacenado "+ sql + " ejecutado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Timestamp convertirStringATimestamp(String fechaString) {
        // Validar que el String no sea nulo o vacío
        if (fechaString == null || fechaString.trim().isEmpty()) {
            System.err.println("Error: La fecha es nula o vacía");
            return null;
        }

        try {
            // Intentar con diferentes formatos de fecha
            SimpleDateFormat formatoFecha;

            // Si contiene barras (/), usar ese formato
            if (fechaString.contains("/")) {
                formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            }
            // Si contiene guiones (-), usar ese formato
            else if (fechaString.contains("-")) {
                formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
            }
            // Por defecto, intentar con barras
            else {
                formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            }

            // Convierte el String a un objeto java.util.Date
            java.util.Date fechaUtil = formatoFecha.parse(fechaString);
            // Convierte el objeto java.util.Date a java.sql.Timestamp
            return new Timestamp(fechaUtil.getTime());
        } catch (ParseException e) {
            // Manejo de errores si la fecha no tiene el formato esperado
            System.err.println("Error al parsear la fecha '" + fechaString + "': " + e.getMessage());
            return null;
        }
    }

    /////// Carga MANUAL //////
    public void configurarCargaLotes() {
        // Deshabilitar botones al inicio
        btnFinalizarCarga.setDisable(true);

        // Validaciones de campos de producto
        lstCargaProductos.addListener((ListChangeListener<RegistroProducto>) change -> {
            btnFinalizarCarga.setDisable(lstCargaProductos.isEmpty());
        });

        colNombreProdLote.setCellValueFactory(new PropertyValueFactory<>("nombrePrenda"));
        colCategoriaProdLote.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colColorProdLote.setCellValueFactory(new PropertyValueFactory<>("color"));
        colTalleProdLote.setCellValueFactory(new PropertyValueFactory<>("talle"));
        colPrecioCompraProdLote.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVentaProdLote.setCellValueFactory(new PropertyValueFactory<>("precioVentaTransf"));

        tblProductosEsteLote.setItems(lstCargaProductos);
    }

    private boolean validarCamposProducto() {
        String mensajeError = "";

        if (fldNombreProductoLote.getText().trim().isEmpty()) {
            mensajeError += "⚠ El campo 'Nombre del Producto' está vacío.\n";
        }
        if (fldCategoriaProductoLote.getText().trim().isEmpty()) {
            mensajeError += "⚠ El campo 'Categoría' está vacío.\n";
        }
        if (fldColorProductoLote.getText().trim().isEmpty()) {
            mensajeError += "⚠ El campo 'Color' está vacío.\n";
        }
        if (fldTalleProductoLote.getText().trim().isEmpty()) {
            mensajeError += "⚠ El campo 'Talle' está vacío.\n";
        }
        if (!fldPrecioCompraProductoLote.getText().matches("\\d+(\\.\\d+)?")) {
            mensajeError += "⚠ El campo 'Precio de Compra' debe ser un número válido.\n";
        }
        if (!fldPrecioVentaProductoLote.getText().matches("\\d+(\\.\\d+)?")) {
            mensajeError += "⚠ El campo 'Precio de Venta' debe ser un número válido.\n";
        }

        // Convertir los valores de precio a números
        double precioCompra = Double.parseDouble(fldPrecioCompraProductoLote.getText());
        double precioVenta = Double.parseDouble(fldPrecioVentaProductoLote.getText());

        // Validar que el precio de venta no sea menor al de compra
        if (precioVenta < precioCompra) {
            mensajeError += ("⚠ El 'Precio de Venta' no puede ser menor que el 'Precio de Compra'.\n");
        }

        // Si hay errores, mostrar alerta
        if (!mensajeError.isEmpty()) {
            mostrarAlerta("Campos Incorrectos", mensajeError, Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void cancelarCargaLote() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro de que desea cancelar la carga del lote?");
        confirmacion.setContentText("Si confirma, los datos ingresados serán eliminados y se reiniciará el formulario.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            limpiarFormulario();
            mostrarAlerta("Carga Cancelada", "Se ha cancelado la carga del lote y se ha reiniciado el formulario.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void agregarProducto() {
        if (!validarCamposProducto()) {
            return;
        }

        // 10% descuento
        RegistroProducto nuevoProducto = new RegistroProducto(
                "Disponible",
                fldNombreProductoLote.getText(),
                fldCategoriaProductoLote.getText(),
                fldColorProductoLote.getText(),
                fldTalleProductoLote.getText(),
                Double.parseDouble(fldPrecioCompraProductoLote.getText()),
                Double.parseDouble(fldPrecioVentaProductoLote.getText()) * 0.9, // 10% descuento
                Double.parseDouble(fldPrecioVentaProductoLote.getText()),
                0
        );

        lstCargaProductos.add(nuevoProducto);
        tblProductosEsteLote.refresh();
        btnFinalizarCarga.setDisable(lstCargaProductos.isEmpty());
        limpiarCamposProducto();
        System.out.println(lstCargaProductos);
    }

    private void insertarLoteYProductos() throws SQLException {

        JSONArray productosJson = new JSONArray();
        double costoLote = 0;

        for (RegistroProducto producto : lstCargaProductos) {
            costoLote += producto.getPrecioCompra();
            JSONObject obj = new JSONObject();
            obj.put("estado", "Disponible");
            obj.put("prenda", producto.getNombrePrenda());
            obj.put("categoria", producto.getCategoria());
            obj.put("color", producto.getColor());
            obj.put("talle", producto.getTalle());
            obj.put("precio_compra", producto.getPrecioCompra());
            obj.put("precio_venta_efectivo", producto.getPrecioVentaEfectivo());
            obj.put("precio_venta_transferencia", producto.getPrecioVentaTransf());

            productosJson.put(obj);
        }

        // Convertir el JSONArray a String
        String productosJsonString = productosJson.toString();

        conectarABBDDConReintentos();
        PreparedStatement stmt = null;

        try {
            conexionABBDD.setAutoCommit(false);
            stmt = conexionABBDD.prepareStatement("CALL sp_inserta_lote_y_productos(?, ?, ?, ?)");
            stmt.setDouble(1, costoLote);
            stmt.setString(2, fldObservacion.getText());
            stmt.setString(3, fldInversor.getText());
            stmt.setString(4, productosJsonString); // Enviar JSON como String

            try {
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error en la carga", "No se pudo insertar el lote y productos: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            conexionABBDD.commit();
            System.out.println("Lote y productos insertados correctamente.");
        } finally {
            if (stmt != null) stmt.close();
            desconexionABBDD();
        }
    }


    @FXML
    private void finalizarCarga() throws SQLException {
        // Validar que los campos obligatorios no estén vacíos
        if (fldObservacion.getText().trim().isEmpty() || fldInversor.getText().trim().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Debes completar los campos de Observación e Inversor para finalizar la carga.", Alert.AlertType.ERROR);
            return;
        }
        // Mostrar cuadro de confirmación antes de proceder
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Carga");
        confirmacion.setHeaderText("¿Está seguro de que desea finalizar la carga del lote?");
        confirmacion.setContentText("Esta acción insertará el lote y los productos en la base de datos.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Insertar lote y productos en la base de datos
                insertarLoteYProductos();

                // Mostrar mensaje de éxito
                mostrarAlerta("Éxito", "Lote y productos cargados correctamente.", Alert.AlertType.INFORMATION);

                // Limpiar formulario después de la carga exitosa
                limpiarFormulario();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar el lote: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            // Usuario canceló la operación
            mostrarAlerta("Cancelado", "La carga del lote ha sido cancelada.", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCamposProducto() {
        fldNombreProductoLote.clear();
        fldCategoriaProductoLote.clear();
        fldColorProductoLote.clear();
        fldTalleProductoLote.clear();
        fldPrecioCompraProductoLote.clear();
        fldPrecioVentaProductoLote.clear();
    }

    private void limpiarFormulario() {
        fldObservacion.clear();
        fldInversor.clear();
        lstCargaProductos.clear();

        btnAgregarProducto.setDisable(true);
        btnFinalizarCarga.setDisable(true);

    }

    //////////// FUNCIONES PESTAÑA VENTAS ////////////////////////
    @FXML
    private void configurarTablaStock() {
        colIdArticuloVentas.setCellValueFactory(new PropertyValueFactory<>("idArticulo"));
        colEstadoVentas.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstadoVentas.setCellFactory(column -> new TableCell<RegistroArticuloVentas, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    if (item.equals("Disponible")) {
                        setTextFill(javafx.scene.paint.Color.GREEN);
                        setStyle("-fx-text-fill: black; -fx-background-color: lightgreen;");
                    } else if (item.equals("Sin stock") || item.equals("Sin Stock")) {
                        setTextFill(javafx.scene.paint.Color.RED);
                        setStyle("-fx-text-fill: black; -fx-background-color: lightcoral;");
                    } else if (item.toLowerCase().contains("pendiente")) {
                        setStyle("-fx-background-color: yellow;");
                    } else {
                        setTextFill(javafx.scene.paint.Color.BLACK);
                        setStyle(""); // Reset style for other cases
                    }
                }
            }
        });
        colNombreArticuloVentas.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));
        colCategoriaVentas.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colColorVentas.setCellValueFactory(new PropertyValueFactory<>("color"));
        colTalleVentas.setCellValueFactory(new PropertyValueFactory<>("talle"));
        colPrecioEfectivoVentas.setCellValueFactory(new PropertyValueFactory<>("precioEfectivo"));
        colPrecioTransferenciaVentas.setCellValueFactory(new PropertyValueFactory<>("precioTransferencia"));
        colAccionesVentas.setCellFactory(column -> new TableCell<>() {
            private final Button btnAgregar = new Button("Agregar al carrito");
            private final Button btnDevolver = new Button("Devolución");

            {
                btnAgregar.setOnAction(event -> {
                    RegistroArticuloVentas articulo = getTableView().getItems().get(getIndex());
                    agregarAlCarrito(articulo);
                });

                btnDevolver.setOnAction(event -> {
                    RegistroArticuloVentas articulo = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmar devolución");
                    alert.setHeaderText("¿Desea realizar la devolución del artículo?");
                    alert.setContentText(
                            "ID: " + articulo.getIdArticulo() + "\n" +
                                    "Nombre: " + articulo.getNombreArticulo() + "\n" +
                                    "Talle: " + articulo.getTalle() + "\n" +
                                    "Color: " + articulo.getColor() + "\n" +
                                    "Precio Transferencia: " + articulo.getPrecioTransferencia()
                    );

                    ButtonType btnConfirmar = new ButtonType("Confirmar");
                    ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(btnConfirmar, btnCancelar);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == btnConfirmar) {
                        devolverArticulo(articulo);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RegistroArticuloVentas articulo = getTableView().getItems().get(getIndex());
                    if ("Disponible".equalsIgnoreCase(articulo.getEstado())) {
                        setGraphic(btnAgregar);
                    } else {
                        setGraphic(btnDevolver);
                    }
                }
            }
        });

        listaFiltrada = new FilteredList<>(listaStock, p -> true);
        tblArticulosStock.setItems(listaFiltrada);
        btnRefrescarStock.setOnAction(event -> refrescarTablaStock());
    }

    @FXML
    private void inicializarFiltrado() {
        fldBuscarNumArticulo.textProperty().addListener((observable, oldValue, newValue) -> filtrarVentas());
        fldBuscarNomPrenda.textProperty().addListener((observable, oldValue, newValue) -> filtrarVentas());
        fldBuscarEstado.textProperty().addListener((observable, oldValue, newValue) -> filtrarVentas());
        fldBuscarCategoria.textProperty().addListener((observable, oldValue, newValue) -> filtrarVentas());
    }

    private void filtrarVentas() {
        String filtroNumArticulo = fldBuscarNumArticulo.getText().trim();
        String filtroNombrePrenda = fldBuscarNomPrenda.getText().trim().toLowerCase();
        String filtroEstado = fldBuscarEstado.getText().trim().toLowerCase();
        String filtroCategoria = fldBuscarCategoria.getText().trim().toLowerCase();

        // Filtrar listaStock según los criterios de búsqueda
        ObservableList<RegistroArticuloVentas> listaFiltrada = FXCollections.observableArrayList();

        for (RegistroArticuloVentas articulo : listaStock) {
            boolean coincideNumArticulo = filtroNumArticulo.isEmpty() ||
                    String.valueOf(articulo.getIdArticulo()).contains(filtroNumArticulo);
            boolean coincideNombrePrenda = filtroNombrePrenda.isEmpty() ||
                    articulo.getNombreArticulo().toLowerCase().contains(filtroNombrePrenda);
            boolean coincideEstado = filtroEstado.isEmpty() ||
                    (articulo.getEstado() != null && articulo.getEstado().toLowerCase().contains(filtroEstado));
            boolean coincideCategoria = filtroCategoria.isEmpty() ||
                    (articulo.getCategoria() != null && articulo.getCategoria().toLowerCase().contains(filtroCategoria));

            if (coincideNumArticulo && coincideNombrePrenda && coincideEstado && coincideCategoria) {
                listaFiltrada.add(articulo);
            }
        }

        // Actualizar la tabla con la lista filtrada
        tblArticulosStock.setItems(listaFiltrada);
    }

    private void configurarTablaCompra() {
        colIdArticuloEstaCompra.setCellValueFactory(new PropertyValueFactory<>("idArticulo"));
        colNomArticuloEstaCompra.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));
        colCategoriaEstaCompra.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colColorEstaCompra.setCellValueFactory(new PropertyValueFactory<>("color"));
        colTalleEstaCompra.setCellValueFactory(new PropertyValueFactory<>("talle"));
        colPrecioEfectivoEstaCompra.setCellValueFactory(new PropertyValueFactory<>("precioEfectivo"));
        colPrecioTransferenciaEstaCompra.setCellValueFactory(new PropertyValueFactory<>("precioTransferencia"));

        // Configurar columna de acciones
        colAccionesEstaCompra.setCellFactory(column -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");
            {
                btnEliminar.setOnAction(event -> {
                    RegistroArticuloVentas articulo = getTableView().getItems().get(getIndex());
                    eliminarDelCarrito(articulo);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });
        tblArticuloEstaCompra.setItems(listaCompra);
    }

    private void cargarDatosDesdeBBDD() {
        listaStock.clear();
        String query = "{CALL sp_consultar_listado_ventas()}";
        conectarABBDDConReintentos();
        try (CallableStatement callableStatement = conexionABBDD.prepareCall(query)) {

            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                RegistroArticuloVentas articuloVentas = new RegistroArticuloVentas(
                        resultSet.getInt("out_id_articulo"),
                        resultSet.getString("out_nombre_articulo"),
                        resultSet.getString("out_estado"),
                        resultSet.getString("out_categoria"),
                        resultSet.getString("out_color"),
                        resultSet.getString("out_talle"),
                        resultSet.getDouble("out_precio_venta_efectivo"),
                        resultSet.getDouble("out_precio_venta_transferencia"),
                        resultSet.getString("out_publicado_en_web")
                );
                listaStock.add(articuloVentas);
            }
            desconexionABBDD();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar datos", "No se pudo obtener la información de la base de datos.", Alert.AlertType.ERROR);
        }
    }

    private void agregarAlCarrito(RegistroArticuloVentas articulo) {
        if (!listaCompra.contains(articulo)) {
            listaCompra.add(articulo);
            totalCompra += articulo.getPrecioTransferencia(); // Actualizar con precio efectivo
            actualizarResumen();
        } else {
            mostrarAlerta("Artículo duplicado", "El artículo ya está en la compra actual.", Alert.AlertType.ERROR);
        }
    }

    private void eliminarDelCarrito(RegistroArticuloVentas articulo) {
        listaCompra.remove(articulo);
        totalCompra -= articulo.getPrecioTransferencia();
        actualizarResumen();
        if(listaCompra.isEmpty()) {
            btnFinalizarCompra.setDisable(true);
            btnCancelarCompra.setDisable(true);
        }
    }

    private void actualizarResumen() {
        lbCantArticulos.setText(String.valueOf(listaCompra.size()));
       /* if (cbMetodoPago.getValue() != null && cbMetodoPago.getValue().equals("Efectivo")) {
            totalCompra = totalCompra * 0.9; // Aplica el 10% de descuento
        }*/
        lbPrecioTotal.setText(totalCompra + " $");
        if(totalCompra>0){
            btnFinalizarCompra.setDisable(false);
            btnCancelarCompra.setDisable(false);
        }else{
            btnFinalizarCompra.setDisable(true);
            btnCancelarCompra.setDisable(true);
        }
    }

    @FXML
    private void refrescarTablaWeb() {
        obtenerProductosDesdeBD();
        filtrarProductosWeb(fldBuscarArticuloWeb.getText());
        tblProductosWeb.refresh();
    }
    @FXML
    private void refrescarTablaStock() {
        fldBuscarNumArticulo.clear();
        fldBuscarNomPrenda.clear();
        fldBuscarEstado.clear();
        fldBuscarCategoria.clear();

        cargarDatosDesdeBBDD();
        listaFiltrada = new FilteredList<>(listaStock, p -> true);
        tblArticulosStock.setItems(listaFiltrada);
        tblArticulosStock.refresh();
    }
    @FXML
    private void cancelarCompra() {
        listaCompra.clear();
        totalCompra = 0.0;
        actualizarResumen();
        if(listaCompra.isEmpty()) {
            btnFinalizarCompra.setDisable(true);
            btnCancelarCompra.setDisable(true);
        }
    }
    @FXML
    private void finalizarCompra() {
        // Obtener datos del cliente (si están vacíos, establecer "No proporcionado")
        nombreCliente = fldNombreCliente.getText().trim().isEmpty() ? "No proporcionado" : fldNombreCliente.getText().trim();
        instagramCliente = fldInstagramCliente.getText().trim().isEmpty() ? "No proporcionado" : fldInstagramCliente.getText().trim();
        correoCliente = fldCorreoCliente.getText().trim().isEmpty() ? "No proporcionado" : fldCorreoCliente.getText().trim();

        // Crear los botones de opción para el método de pago
        RadioButton rbEfectivo = new RadioButton("Efectivo");
        RadioButton rbTransferencia = new RadioButton("Transferencia");
        rbTransferencia.setSelected(true); // Transferencia por defecto

        ToggleGroup grupoPago = new ToggleGroup();
        rbEfectivo.setToggleGroup(grupoPago);
        rbTransferencia.setToggleGroup(grupoPago);

        // Contenedor de botones de pago
        VBox vboxPago = new VBox(10, rbEfectivo, rbTransferencia);
        vboxPago.setAlignment(Pos.CENTER_LEFT);

        // Contenedor para el resumen
        VBox vboxResumen = new VBox(15, vboxPago);
        vboxResumen.setAlignment(Pos.CENTER);

        // Generar el resumen de la compra
        StringBuilder resumenCompra = new StringBuilder();
        resumenCompra.append("Cliente: ").append(nombreCliente).append("\n")
                .append("Instagram: ").append(instagramCliente).append("\n")
                .append("Correo: ").append(correoCliente).append("\n\n")
                .append("Detalle de compra:\n");

        double totalCompra = 0;

        for (RegistroArticuloVentas producto : listaCompra) {
            double precio = rbTransferencia.isSelected() ?
                    producto.getPrecioTransferencia() :
                    producto.getPrecioEfectivo();
            totalCompra += precio;
            resumenCompra.append(String.format("- %s: $%.2f\n", producto.getNombreArticulo(), precio));
        }

        // Aplicar descuento si se selecciona efectivo
        double finalTotalCompra = totalCompra;
        rbEfectivo.setOnAction(event -> {
            double totalConDescuento = finalTotalCompra * 0.9;
            lbPrecioTotal.setText(String.format("$%.2f", totalConDescuento));
        });

        double finalTotalCompra1 = totalCompra;
        rbTransferencia.setOnAction(event -> {
            lbPrecioTotal.setText(String.format("$%.2f", finalTotalCompra1));
        });

        // Inicializar con el valor por defecto (Transferencia)
        lbPrecioTotal.setText(String.format("$%.2f", totalCompra));

        resumenCompra.append("\nTotal: $").append(String.format("%.2f", totalCompra));

        // Mostrar ventana con el resumen de la compra
        mostrarResumenCompra(grupoPago.getSelectedToggle() == rbEfectivo ? "Efectivo" : "Transferencia", resumenCompra.toString());
    }
    @FXML
    private void devolverArticulo(RegistroArticuloVentas articulo) {
        try {
            conectarABBDDConReintentos(); // Asegurate de tener esta función
            CallableStatement stmt = conexionABBDD.prepareCall("CALL sp_devolver_articulo(?)");
            stmt.setInt(1, articulo.getIdArticulo());
            stmt.execute();
            mostrarAlerta("Informativo","Articulo devuelto correctamente.", Alert.AlertType.INFORMATION);
            refrescarTablaStock(); // Vuelve a cargar la tabla
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error","Error al devolver el artículo.: " + e.getMessage() , Alert.AlertType.ERROR);
        }
    }



    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarResumenCompra(String metodoPagoInicial, String resumen) {
        // Crear diálogo personalizado
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Resumen de Compra");

        // Crear los RadioButtons para el método de pago
        RadioButton rbEfectivo = new RadioButton("Efectivo");
        RadioButton rbTransferencia = new RadioButton("Transferencia");
        rbTransferencia.setSelected(true); // Transferencia por defecto

        ToggleGroup grupoPago = new ToggleGroup();
        rbEfectivo.setToggleGroup(grupoPago);
        rbTransferencia.setToggleGroup(grupoPago);

        // Etiqueta para el total
        Label lbTotal = new Label();

        // Calcular total inicial basado en el método de pago
        double totalCompra = calcularTotal(listaCompra, "Transferencia"); // Transferencia por defecto
        lbTotal.setText(String.format("Total: $%.2f", totalCompra));
        lbTotal.setStyle("-fx-font-weight: bold;");

        // Evento para actualizar el total si se cambia el método de pago
        rbEfectivo.setOnAction(event -> {
            double totalConDescuento = calcularTotal(listaCompra, "Efectivo");
            lbTotal.setText(String.format("Total: $%.2f", totalConDescuento));
        });

        rbTransferencia.setOnAction(event -> {
            lbTotal.setText(String.format("Total: $%.2f", calcularTotal(listaCompra, "Transferencia")));
        });

        // Crear un VBox con los elementos
        VBox contenido = new VBox(10,
                new Label("Seleccione método de pago:"),
                rbEfectivo, rbTransferencia,
                new Label("Resumen de la compra:"),
                new TextArea(resumen),
                lbTotal
        );
        contenido.setPadding(new Insets(10));

        // Agregar contenido al DialogPane
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        // Mostrar la ventana y procesar la acción del usuario
        dialogo.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String metodoPagoSeleccionado = rbEfectivo.isSelected() ? "Efectivo" : "Transferencia";
                double totalFinal = rbEfectivo.isSelected() ? calcularTotal(listaCompra, "Efectivo") : calcularTotal(listaCompra, "Transferencia");
                confirmarCompra(metodoPagoSeleccionado, totalFinal, listaCompra);
            }
        });
    }

    private double calcularTotal(List<RegistroArticuloVentas> listaCompra, String metodoPago) {
        double total = 0;
        for (RegistroArticuloVentas producto : listaCompra) {
            total += metodoPago.equals("Efectivo") ? producto.getPrecioEfectivo() : producto.getPrecioTransferencia();
        }
        return total;
    }

    private void confirmarCompra(String metodoPago, double totalCompra, ObservableList<RegistroArticuloVentas> listaCompra) {
        conectarABBDDConReintentos();

        if (conexionABBDD != null) {
            try {
                conexionABBDD.setAutoCommit(false);

                Integer[] arrayIds = listaCompra.stream().map(RegistroArticuloVentas::getIdArticulo).toArray(Integer[]::new);
                Array sqlIdArray = conexionABBDD.createArrayOf("INTEGER", arrayIds);

                String sqlInsertarPago = "CALL public.sp_procesar_venta(?, ?, ?, ?)";
                try (PreparedStatement stmtPago = conexionABBDD.prepareStatement(sqlInsertarPago)) {
                    stmtPago.setDouble(1, totalCompra);
                    stmtPago.setString(2, nombreCliente);
                    stmtPago.setString(3, metodoPago);
                    stmtPago.setArray(4, sqlIdArray);
                    stmtPago.execute();
                }

                conexionABBDD.commit();
                mostrarAlerta("Compra confirmada", "La compra se ha registrado exitosamente.", Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                // Revertir la transacción en caso de error
                try {
                    if (conexionABBDD != null) {
                        conexionABBDD.rollback();
                        mostrarAlerta("Error", "Se ha revertido la transacción debido a un error: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                } catch (SQLException rollbackEx) {
                    mostrarAlerta("Error Crítico", "No se pudo revertir la transacción: " + rollbackEx.getMessage(), Alert.AlertType.ERROR);
                }
            } finally {
                // Restaurar auto-commit y cerrar conexión
                try {
                    if (conexionABBDD != null) {
                        conexionABBDD.setAutoCommit(true);
                        desconexionABBDD();
                    }
                } catch (SQLException closeEx) {
                    mostrarAlerta("Error al Cerrar", "No se pudo restaurar auto-commit o cerrar la conexión: " + closeEx.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
        desconexionABBDD();
        refrescarTablaStock();
        limpiarTablaEstaCompra();

    }
    private void limpiarTablaEstaCompra(){
        tblArticuloEstaCompra.getItems().clear();
        actualizarResumen();
        lbPrecioTotal.setText("");
        btnFinalizarCompra.setDisable(true);
        btnCancelarCompra.setDisable(true);

    }

    //////////// FUNCIONES PESTAÑA CASHFLOW ////////////////////////
    private void configurarTablaCashFlow() {
        colFechaTransaccion.setCellValueFactory(new PropertyValueFactory<>("fechaTransaccion"));
        colDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));
        colHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));
        btnConsultarCF.setDisable(true);
        dtFechaInicialCF.valueProperty().addListener((obs, oldValue, newValue) -> validarFechas());
        dtFechaFinalCF.valueProperty().addListener((obs, oldValue, newValue) -> validarFechas());
        // Vincular el botón con la ejecución de la consulta
        btnConsultarCF.setOnAction(event -> ejecutarConsulta());
        btnLimpiarCF.setOnAction(event -> limpiarCashflow());
    }

    private void validarFechas() {
        LocalDate fechaInicio = dtFechaFinalCF.getValue();
        LocalDate fechaFin = dtFechaFinalCF.getValue();

        // Habilita el botón solo si ambas fechas están seleccionadas
        btnConsultarCF.setDisable(fechaInicio == null && fechaFin == null);
    }

    @FXML
    private void ejecutarConsulta() {
        lstObsCashflow.clear();

        LocalDate fechaInicio = dtFechaInicialCF.getValue();
        LocalDate fechaFin = dtFechaFinalCF.getValue();

        // Validar intervalo de fechas
        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta("Error", "Intervalo de fechas incorrecto", Alert.AlertType.ERROR);
            return;
        }

        String sql = "SELECT * FROM sp_analizar_cashflow(?, ?, ?)";

        conectarABBDDConReintentos();

        try (PreparedStatement pstmt = conexionABBDD.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            pstmt.setInt(3, 1); // Opción 1

            try (ResultSet rs = pstmt.executeQuery()) {
                Double[] metrics = null; // Variable para almacenar las métricas

                while (rs.next()) {
                    String observacion = rs.getString("observacion");

                    // Normalizar la observación (elimina acentos y compara sin distinguir mayúsculas)
                    String observacionNormalizada = Normalizer.normalize(observacion.trim(), Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "");

                    Array analisisArray = rs.getArray("analisis");

                    // Si es la fila de análisis, guardar las métricas pero NO agregarla a la tabla
                    if ("ANALISIS".equalsIgnoreCase(observacionNormalizada)) {
                        if (analisisArray != null) {
                            metrics = (Double[]) analisisArray.getArray();
                        }
                        continue; // Salta esta fila para que no aparezca en la tabla
                    }

                    // Agregar la fila normal a la tabla
                    RegistroTransaccionCF t = new RegistroTransaccionCF(
                            rs.getString("fecha_transaccion"),
                            rs.getDouble("debe"),
                            rs.getDouble("haber"),
                            rs.getDouble("saldo"),
                            observacion,
                            analisisArray
                    );
                    lstObsCashflow.add(t);
                }

                // Aplicar las métricas si se capturaron
                if (metrics != null) {
                    Double[] finalMetrics = metrics; // Para usar en runLater
                    Platform.runLater(() -> {
                        lbCantVentas.setText(String.valueOf(finalMetrics[0].intValue())); // Cantidad de ventas
                        lbTotalIngresos.setText(String.format("$%.2f", finalMetrics[1])); // Total ingresos
                        lbTotalGastos.setText(String.format("$%.2f", finalMetrics[2])); // Total gastos
                        lbSaldoTotal.setText(String.format("$%.2f", finalMetrics[3])); // Saldo total
                        lbCantGasto.setText(String.valueOf(finalMetrics[4].intValue())); // Cantidad de gastos
                        lbMayorIngreso.setText(String.format("$%.2f", finalMetrics[7])); // Mayor ingreso

                        // Convertir los timestamps a fecha legible
                        LocalDate diaMaxIngreso = Instant.ofEpochSecond(finalMetrics[8].longValue()).atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate diaMaxGasto = Instant.ofEpochSecond(finalMetrics[9].longValue()).atZone(ZoneId.systemDefault()).toLocalDate();

                        lbDiaMayorIngreso.setText(diaMaxIngreso.toString()); // Día mayor ingreso
                        lbDiaMayorGasto.setText(diaMaxGasto.toString()); // Día mayor gasto
                    });
                }
            }
            tblCashFlow.setItems(lstObsCashflow);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        desconexionABBDD();
    }

    @FXML
    private void limpiarCashflow() {
        tblCashFlow.getItems().clear();
        lbCantVentas.setText("");
        lbTotalIngresos.setText("");
        lbTotalGastos.setText("");
        lbSaldoTotal.setText("");
        lbMayorIngreso.setText("");
        lbCantGasto.setText("");
        lbDiaMayorIngreso.setText("");
        lbDiaMayorGasto.setText("");
        dtFechaInicialCF.setValue(null);
        dtFechaFinalCF.setValue(null);
        btnConsultarCF.setDisable(true);
    }

    private Connection conectarABBDD() throws SQLException {
        return DatabaseConfig.createConnection();
    }

    public void conectarABBDDConReintentos() {

        int intentos = 0;
        while (intentos < 3) {
            try {
                this.conexionABBDD = conectarABBDD();
                System.out.println("Conexión exitosa a la base de datos.");
                return;
            } catch (SQLException e) {
                intentos++;
                System.err.println("Error al conectar con PostgreSQL. Intento " + intentos + " de 3");
                e.printStackTrace();
            }
        }
        System.err.println("No se pudo conectar después de 3 intentos.");
        this.conexionABBDD = null;
    }

    public void desconexionABBDD(){
        if(conexionABBDD!=null){
            try{
                conexionABBDD.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Verifica si hay conexión a la base de datos antes de realizar operaciones
     * @return true si hay conexión, false si está en modo offline
     */
    private boolean verificarConexionBD() {
        if (modoOffline || conexionABBDD == null) {
            Platform.runLater(() -> {
                mostrarAlerta("Modo Offline",
                    "Esta función requiere conexión a la base de datos.\n" +
                    "Actualmente la aplicación está en modo offline.",
                    Alert.AlertType.INFORMATION);
            });
            return false;
        }
        return true;
    }

}
