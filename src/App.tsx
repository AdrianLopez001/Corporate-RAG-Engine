import { useState, useRef, useEffect } from 'react';
import { Search, Upload, FileText, Send, Database, CheckCircle2, ChevronRight, FileCode, Layers, Loader2, X, MessageSquare, Download } from 'lucide-react';

export default function App() {
  const [query, setQuery] = useState('');
  const [chat, setChat] = useState<{ role: 'user' | 'assistant', text: string, status?: string }[]>([
    { role: 'assistant', text: 'Olá! Sou o assistente corporativo (RAG Engine). Faça perguntas sobre os manuais técnicos, relatórios financeiros ou políticas de RH indexadas na nossa base de conhecimento vetorial.' }
  ]);
  const [isTyping, setIsTyping] = useState(false);
  const [status, setStatus] = useState('');
  const [selectedDoc, setSelectedDoc] = useState<{ name: string, size: string, type: string } | null>(null);
  const bottomRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chat, status]);

  const handleSend = () => {
    if (!query.trim()) return;
    const userText = query;
    setChat(prev => [...prev, { role: 'user', text: userText }]);
    setQuery('');
    setIsTyping(true);
    
    // Simulate RAG Pipeline
    setStatus('Extraindo embeddings da consulta (OpenAI text-embedding-3-small)...');
    
    setTimeout(() => {
      setStatus('Buscando contexto relevante no banco vetorial (pgvector HNSW)...');
      
      setTimeout(() => {
        setStatus('Sintetizando resposta final via GPT-4o-mini com o contexto recuperado...');
        
        setTimeout(() => {
          setStatus('');
          setIsTyping(false);
          setChat(prev => [...prev, { 
            role: 'assistant', 
            text: `Baseado nos documentos corporativos, aqui está a análise sobre "${userText}". O sistema de RAG identificou as principais diretrizes no **Manual de Operações v3.2** (página 45) e no **Relatório Q3** confirmando que os procedimentos técnicos devem seguir a norma ABNT vigente.`,
          }]);
        }, 1500);
      }, 1500);
    }, 1500);
  };

  return (
    <div className="flex h-screen bg-[#0d1117] text-gray-200 font-sans w-full absolute top-0 left-0 right-0 bottom-0">
      {/* Sidebar */}
      <div className="w-72 bg-[#161b22] border-r border-[#30363d] flex flex-col">
        <div className="p-4 border-b border-[#30363d] flex items-center gap-3">
          <div className="w-8 h-8 rounded bg-blue-600 flex items-center justify-center">
            <Database size={16} className="text-white" />
          </div>
          <h1 className="font-semibold text-sm tracking-wide text-white">Corporate RAG</h1>
        </div>
        
        <div className="p-4 flex-1 overflow-y-auto">
          <div className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">Conhecimento Indexado</div>
          
          <div className="space-y-2">
            {[
              { name: 'Manual_Operacoes_v3.pdf', size: '2.4 MB', type: 'PDF' },
              { name: 'Relatorio_Q3_Financeiro.xlsx', size: '1.1 MB', type: 'XLSX' },
              { name: 'Politicas_RH_2026.docx', size: '450 KB', type: 'DOCX' },
              { name: 'Arquitetura_Microservicos.md', size: '12 KB', type: 'MD' },
            ].map(doc => {
              const isSelected = selectedDoc?.name === doc.name;
              return (
                <div
                  key={doc.name}
                  onClick={() => setSelectedDoc(isSelected ? null : doc)}
                  className={`flex items-center gap-3 p-2 rounded-md cursor-pointer transition-all duration-200 border ${
                    isSelected
                      ? 'bg-blue-600/10 border-blue-500/50 text-white shadow-sm shadow-blue-500/5'
                      : 'border-transparent hover:bg-[#21262d] hover:border-[#30363d] text-gray-300'
                  }`}
                >
                  <FileText size={16} className={doc.type === 'PDF' ? 'text-red-400' : doc.type === 'XLSX' ? 'text-green-400' : 'text-blue-400'} />
                  <div className="flex-1 overflow-hidden">
                    <div className={`text-sm truncate ${isSelected ? 'font-medium text-blue-400' : 'text-gray-300'}`}>{doc.name}</div>
                    <div className="text-xs text-gray-500 flex items-center gap-1">
                      <CheckCircle2 size={10} className="text-green-500" />
                      Indexado (HNSW) • {doc.size}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
          
          <button className="mt-4 w-full flex items-center justify-center gap-2 py-2 px-4 rounded-md border border-dashed border-[#30363d] text-gray-400 hover:text-white hover:border-gray-500 transition-colors text-sm">
            <Upload size={14} />
            Ingerir Documento
          </button>
        </div>
        
        <div className="p-4 border-t border-[#30363d] bg-[#0d1117]">
          <div className="flex items-center gap-2 text-xs text-gray-400">
            <Layers size={14} />
            <span>Stack: Java 21, Spring AI, pgvector</span>
          </div>
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col bg-[#0d1117] relative">
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
           <div className="absolute -top-[20%] -right-[10%] w-[500px] h-[500px] rounded-full bg-blue-900/20 blur-[120px]" />
        </div>
        
        <div className="flex-1 overflow-y-auto p-8 z-10 flex flex-col gap-6">
          {chat.map((msg, i) => (
            <div key={i} className={`flex gap-4 max-w-3xl ${msg.role === 'user' ? 'ml-auto' : ''}`}>
              {msg.role === 'assistant' && (
                <div className="w-8 h-8 rounded bg-blue-600/20 border border-blue-500/30 flex items-center justify-center shrink-0 mt-1">
                  <FileCode size={16} className="text-blue-400" />
                </div>
              )}
              
              <div className={`p-4 rounded-lg text-sm leading-relaxed ${
                msg.role === 'user' 
                  ? 'bg-blue-600 text-white shadow-md' 
                  : 'bg-[#161b22] border border-[#30363d] text-gray-300 shadow-sm'
              }`}>
                {msg.text}
              </div>
            </div>
          ))}
          
          {isTyping && (
            <div className="flex gap-4 max-w-3xl">
               <div className="w-8 h-8 rounded bg-blue-600/20 border border-blue-500/30 flex items-center justify-center shrink-0 mt-1">
                  <FileCode size={16} className="text-blue-400" />
                </div>
                <div className="p-4 rounded-lg bg-[#161b22] border border-[#30363d] flex flex-col gap-2">
                  <div className="flex items-center gap-3 text-blue-400 text-sm font-medium">
                    <Loader2 size={16} className="animate-spin" />
                    Processando pipeline RAG...
                  </div>
                  <div className="text-xs text-gray-500 font-mono flex items-center gap-2">
                    <ChevronRight size={12} /> {status}
                  </div>
                </div>
            </div>
          )}
          <div ref={bottomRef} />
        </div>
        
        <div className="p-6 pt-0 z-10">
          <div className="max-w-3xl mx-auto relative group">
            <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl blur opacity-20 group-hover:opacity-40 transition-opacity" />
            <div className="relative flex items-center bg-[#161b22] border border-[#30363d] rounded-xl overflow-hidden focus-within:border-blue-500 transition-colors shadow-lg">
              <div className="pl-4 text-gray-500">
                <Search size={18} />
              </div>
              <input 
                ref={inputRef}
                type="text" 
                value={query}
                onChange={e => setQuery(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleSend()}
                placeholder="Ex: Quais são as regras de compliance no relatório Q3?"
                className="w-full bg-transparent border-none text-sm text-gray-200 p-4 focus:outline-none placeholder-gray-500"
              />
              <button 
                onClick={handleSend}
                disabled={!query.trim() || isTyping}
                className="p-4 text-gray-400 hover:text-blue-400 disabled:opacity-50 transition-colors"
              >
                <Send size={18} />
              </button>
            </div>
          </div>
          <div className="text-center mt-3 text-xs text-gray-600">
             Corporate RAG Engine — UI de demonstração. O processamento real ocorre no backend Java.
          </div>
        </div>
      </div>

      {/* Document Preview Side Panel */}
      {selectedDoc && (
        <div className="w-[450px] bg-[#161b22] border-l border-[#30363d] flex flex-col z-20 transition-all duration-300">
          {/* Header */}
          <div className="p-4 border-b border-[#30363d] flex items-center justify-between">
            <div className="flex items-center gap-2">
              <FileText size={18} className={selectedDoc.type === 'PDF' ? 'text-red-400' : selectedDoc.type === 'XLSX' ? 'text-green-400' : 'text-blue-400'} />
              <div className="overflow-hidden">
                <h2 className="font-semibold text-sm text-white truncate max-w-[280px]" title={selectedDoc.name}>{selectedDoc.name}</h2>
                <p className="text-xs text-gray-500">Visualização de Conteúdo</p>
              </div>
            </div>
            <button onClick={() => setSelectedDoc(null)} className="text-gray-400 hover:text-white p-1.5 rounded hover:bg-[#21262d] transition-colors">
              <X size={16} />
            </button>
          </div>

          {/* Content */}
          <div className="flex-1 overflow-y-auto p-6 space-y-4">
            {renderDocContent(selectedDoc)}
          </div>

          {/* Footer Actions */}
          <div className="p-4 border-t border-[#30363d] bg-[#0d1117] flex gap-2">
            <button 
              onClick={() => {
                setQuery(`Me dê um resumo detalhado sobre o documento ${selectedDoc.name}`);
                inputRef.current?.focus();
              }} 
              className="flex-1 py-2 px-3 rounded-md bg-blue-600 text-white text-xs font-semibold hover:bg-blue-500 transition-colors flex items-center justify-center gap-1.5 cursor-pointer"
            >
              <MessageSquare size={13} />
              Perguntar sobre este doc
            </button>
            <button className="py-2 px-3 rounded-md bg-[#21262d] border border-[#30363d] text-gray-300 text-xs font-semibold hover:text-white hover:bg-[#30363d] transition-colors flex items-center justify-center gap-1.5 cursor-pointer">
              <Download size={13} />
              Baixar
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

function renderDocContent(doc: { name: string, type: string }) {
  switch (doc.type) {
    case 'PDF':
      return (
        <div className="bg-[#0d1117] border border-[#30363d] rounded-lg p-5 font-sans space-y-4 text-sm shadow-inner">
          <div className="border-b border-[#30363d] pb-2 mb-2">
            <span className="text-xs font-mono text-red-400">Pág 1/3 • MANUAL DE OPERAÇÕES</span>
            <h3 className="text-base font-bold text-white mt-1">1. Diretrizes de Operação dos Servidores</h3>
          </div>
          <p className="text-gray-300 leading-relaxed text-xs">
            Este documento estabelece as diretrizes padrão para administração e manutenção de servidores corporativos da infraestrutura de RAG. Todas as instruções contidas aqui devem ser rigorosamente seguidas pelos operadores.
          </p>
          <div className="space-y-2 mt-4">
            <h4 className="text-xs font-bold text-white">1.1. Monitoramento de Recursos</h4>
            <p className="text-gray-400 leading-relaxed text-xs">
              O uso de CPU, RAM e disco deve ser monitorado constantemente através dos painéis integrados. Alarmes automáticos de uso acima de 85% são enviados via Webhook.
            </p>
          </div>
          <div className="space-y-2">
            <h4 className="text-xs font-bold text-white">1.2. Escalabilidade do Banco Vetorial</h4>
            <p className="text-gray-400 leading-relaxed text-xs">
              Ao indexar novos documentos, certifique-se de que o uso do pgvector com HNSW não cause latência na busca de embeddings. Recomenda-se realizar o re-index a cada 100k registros.
            </p>
          </div>
        </div>
      );
    case 'XLSX':
      return (
        <div className="space-y-4">
          <div className="bg-[#0d1117] border border-[#30363d] rounded-lg overflow-hidden shadow-inner">
            <div className="bg-[#1f242c] p-2 text-xs font-mono text-green-400 border-b border-[#30363d] flex items-center justify-between">
              <span>Aba: "Resultados_Consolidados"</span>
              <span className="text-gray-500">3 colunas x 4 linhas</span>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs border-collapse">
                <thead>
                  <tr className="bg-[#161b22] border-b border-[#30363d] text-gray-400">
                    <th className="p-3 border-r border-[#30363d]">Mês</th>
                    <th className="p-3 border-r border-[#30363d]">Receita</th>
                    <th className="p-3">Despesas</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#30363d] text-gray-300">
                  <tr>
                    <td className="p-3 font-medium border-r border-[#30363d]">Julho</td>
                    <td className="p-3 text-green-400 border-r border-[#30363d]">R$ 450.000,00</td>
                    <td className="p-3 text-red-400">R$ 310.000,00</td>
                  </tr>
                  <tr className="bg-[#161b22]/50">
                    <td className="p-3 font-medium border-r border-[#30363d]">Agosto</td>
                    <td className="p-3 text-green-400 border-r border-[#30363d]">R$ 480.000,00</td>
                    <td className="p-3 text-red-400">R$ 305.000,00</td>
                  </tr>
                  <tr>
                    <td className="p-3 font-medium border-r border-[#30363d]">Setembro</td>
                    <td className="p-3 text-green-400 border-r border-[#30363d]">R$ 510.000,00</td>
                    <td className="p-3 text-red-400">R$ 320.000,00</td>
                  </tr>
                  <tr className="bg-[#21262d] font-bold border-t border-[#30363d] text-white">
                    <td className="p-3 border-r border-[#30363d]">Total</td>
                    <td className="p-3 text-green-400 border-r border-[#30363d]">R$ 1.440.000,00</td>
                    <td className="p-3 text-red-400">R$ 935.000,00</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div className="bg-green-500/10 border border-green-500/20 rounded-md p-3 text-xs text-green-400 leading-relaxed">
            💡 **Nota do RAG:** Os dados indicam um aumento constante de receita de 6.67% ao mês no terceiro trimestre de 2026.
          </div>
        </div>
      );
    case 'DOCX':
      return (
        <div className="bg-[#0d1117] border border-[#30363d] rounded-lg p-5 space-y-4 text-xs shadow-inner">
          <div className="border-b border-[#30363d] pb-2 mb-2">
            <span className="text-xs font-mono text-blue-400">DOCUMENTO RH • CONFIDENCIAL</span>
            <h3 className="text-base font-bold text-white mt-1">Políticas de Trabalho Híbrido 2026</h3>
          </div>
          <p className="text-gray-300 leading-relaxed">
            Este documento formaliza as regras aplicáveis para o trabalho no modelo híbrido, vigentes a partir de Janeiro de 2026.
          </p>
          <div className="space-y-2 mt-4">
            <h4 className="font-bold text-white">1. Modelo Híbrido 3x2</h4>
            <p className="text-gray-400 leading-relaxed">
              Os colaboradores de tecnologia deverão comparecer presencialmente ao escritório 3 vezes por semana (Terça, Quarta e Quinta), com os dias de Segunda e Sexta liberados para home office opcional.
            </p>
          </div>
          <div className="space-y-2">
            <h4 className="font-bold text-white">2. Equipamentos e Infraestrutura</h4>
            <p className="text-gray-400 leading-relaxed">
              A empresa disponibilizará notebook, monitor extra e periféricos. O auxílio home-office será de R$ 150,00 mensais para custos de internet e energia.
            </p>
          </div>
        </div>
      );
    case 'MD':
      return (
        <div className="space-y-3">
          <div className="bg-[#0d1117] border border-[#30363d] rounded-lg p-4 font-mono text-[11px] leading-relaxed text-gray-300 overflow-x-auto space-y-2 shadow-inner">
            <div><span className="text-blue-400"># Visão Geral da Arquitetura</span></div>
            <div>A arquitetura segue o modelo de microserviços orientados a eventos.</div>
            <br />
            <div><span className="text-blue-400">## Componentes Principais</span></div>
            <div>1. <span className="text-purple-400">**API Gateway**</span>: Ponto de entrada de requisições rest.</div>
            <div>2. <span className="text-purple-400">**Auth Service**</span>: Autenticação via JWT.</div>
            <div>3. <span className="text-purple-400">**RAG Service**</span>: Core da IA, responsável por orquestrar os prompts e ler o banco PostgreSQL + pgvector.</div>
            <br />
            <div>{"```"}mermaid</div>
            <div>graph TD</div>
            <div className="pl-4">A[Pergunta do Usuário] --&gt; B(Busca Vetorial pgvector)</div>
            <div className="pl-4">B --&gt; C{"{"}Contexto Encontrado?{"}"}</div>
            <div className="pl-4">C -- Sim --&gt; D[Montar Prompt Completo]</div>
            <div className="pl-4">D --&gt; E[Envio para GPT-4o-mini]</div>
            <div className="pl-4">E --&gt; F[Resposta Final ao Usuário]</div>
            <div>{"```"}</div>
          </div>
        </div>
      );
    default:
      return <div className="text-gray-500 text-sm">Sem pré-visualização disponível.</div>;
  }
}
