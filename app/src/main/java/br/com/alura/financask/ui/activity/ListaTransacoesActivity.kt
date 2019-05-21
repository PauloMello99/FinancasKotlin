package br.com.alura.financask.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.AdapterView
import br.com.alura.financask.R
import br.com.alura.financask.dao.TransacaoDAO
import br.com.alura.financask.model.Tipo
import br.com.alura.financask.ui.ResumoView
import br.com.alura.financask.ui.adapter.ListaTransacoesAdapter
import br.com.alura.financask.ui.dialog.AdicionaTransacaoDialog
import br.com.alura.financask.ui.dialog.AlteraTransacaoDialog
import kotlinx.android.synthetic.main.activity_lista_transacoes.*

class ListaTransacoesActivity : AppCompatActivity() {

    private val dao = TransacaoDAO()
    private val transacoes = dao.transacoes
    private val viewActivity by lazy {
        window.decorView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_transacoes)

        configuraResumo()
        configuraLista()
        configuraFab()
    }

    private fun configuraFab() {
        lista_transacoes_adiciona_receita
                .setOnClickListener {
                    chamaDialogDeAdicao(Tipo.RECEITA)
                }
        lista_transacoes_adiciona_despesa
                .setOnClickListener {
                    chamaDialogDeAdicao(Tipo.DESPESA)
                }
    }

    private fun chamaDialogDeAdicao(tipo: Tipo) {
        AdicionaTransacaoDialog(viewActivity as ViewGroup, this)
            .chama(tipo) { transacao ->
                dao.adiciona(transacao)
                atualizaTransacoes()
                lista_transacoes_adiciona_menu.close(true)
            }
    }

    private fun atualizaTransacoes() {
        configuraLista()
        configuraResumo()
    }

    private fun configuraResumo() {
        val resumoView = ResumoView(this, viewActivity, transacoes)
        resumoView.atualiza()
    }

    private fun configuraLista() {
        lista_transacoes_listview.adapter = ListaTransacoesAdapter(transacoes, this)
        lista_transacoes_listview.setOnItemClickListener { _, _, posicao, _ ->
            val transacao = transacoes[posicao]
            AlteraTransacaoDialog(viewActivity as ViewGroup, this)
                    .chama(transacao) { transacaoCriada ->
                        dao.altera(transacaoCriada,posicao)
                        atualizaTransacoes()
                    }
        }
        lista_transacoes_listview.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add(Menu.NONE,0,Menu.NONE,"Remover")
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == 0){
            val adapterMenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
            val posicao = adapterMenuInfo.position
            removeItem(posicao)
        }
        return super.onContextItemSelected(item)
    }

    private fun removeItem(posicao: Int) {
        dao.remove(posicao)
        atualizaTransacoes()
    }

}